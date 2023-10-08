package rmiproject;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RemoteArrayTest {
    private RemoteStringArrayImpl remoteArray;

    @BeforeEach
    public void setUp() throws RemoteException {
        int capacity = 5;
        remoteArray = new RemoteStringArrayImpl(capacity);
        String[] initVals = { "a", "b", "c" };
        IntStream.range(0, initVals.length).forEach(i -> {
            try {
                remoteArray.insertArrayElement(i, initVals[i]);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    void testRemoteArrayCapacityPostInsert() throws RemoteException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        ArrayList<String> expectedArr = new ArrayList<>(Arrays.asList("a", "b", "c", "", ""));

        Field remoteStrArrField = RemoteStringArrayImpl.class.getDeclaredField("array");
        remoteStrArrField.setAccessible(true);
        CopyOnWriteArrayList<ArrayItem> actualArr = (CopyOnWriteArrayList<ArrayItem>) remoteStrArrField
                .get(remoteArray);

        ArrayList<String> valuesList = (ArrayList<String>) actualArr.stream()
                .map(ArrayItem::getValue)
                .collect(Collectors.toCollection(ArrayList::new));

        assertArrayEquals(expectedArr.toArray(), valuesList.toArray());
        assertEquals(5, valuesList.size());
    }
}
