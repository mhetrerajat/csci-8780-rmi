package rmiproject;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
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
        ArrayList<String> actualArr = (ArrayList<String>) remoteStrArrField.get(remoteArray);

        assertArrayEquals(expectedArr.toArray(), actualArr.toArray());
        assertEquals(5, actualArr.size());
    }
}
