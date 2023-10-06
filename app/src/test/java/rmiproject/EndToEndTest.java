package rmiproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EndToEndTest {
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
    void testReadWriteReadMultiClient() throws RemoteException, InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2); // Two threads for two clients
        Integer index = 1;

        // Read index element with Client 1
        String readVal = remoteArray.fetchElementRead(index, 111);

        // Read[with write perms] index element with Client 2 concurrently
        Future<String> writeOpVal = executorService
                .submit(() -> remoteArray.fetchElementWrite(index, 222));

        assertEquals("b", readVal); // should fetch index element
        assertNull(writeOpVal.get()); // should get the value

        // Client 1 release locks
        remoteArray.releaseLock(index, 111);

        // Read[with write perms] index element with Client 2
        Future<String> writeOpAttemptTwoVal = executorService
                .submit(() -> remoteArray.fetchElementWrite(index, 222));

        assertEquals("b", writeOpAttemptTwoVal.get()); // should get the value now
    }
}
