package rmiproject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WriteLockTest {
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
    public void testWriteLockAcquisitionSuccess() throws RemoteException {
        // Scenario: Try to get write lock when it is not being acquired anyone
        boolean result = remoteArray.requestWriteLock(1, 123);
        // Verify that the method returns true when the lock is acquired successfully
        assertTrue(result);
    }

    @Test
    public void testWriteLockAlreadyTakenBySameClient() throws RemoteException {
        // Scenario: Try to get write lock when it is already acquired by same client

        remoteArray.requestWriteLock(2, 789); // get the write lock
        boolean result = remoteArray.requestWriteLock(2, 789); // retry to get the same
        assertTrue(result);
    }

    @Test
    public void testWriteLockAlreadyTakenByDifferentClient()
            throws RemoteException, InterruptedException, ExecutionException {

        // Scenario: When two clients try to acquire same lock simultaneously.

        ExecutorService executorService = Executors.newFixedThreadPool(2); // Two threads for two clients

        // give write lock first
        Future<Boolean> writeLockResultClientOne = executorService.submit(() -> remoteArray.requestWriteLock(2, 789));
        // try to get same lock for different client
        Future<Boolean> writeLockResultClientTwo = executorService.submit(() -> remoteArray.requestWriteLock(2, 22));

        // Get the results from the threads
        boolean writeLockAcquiredClientOne = writeLockResultClientOne.get();
        boolean writeLockAcquiredClientTwo = writeLockResultClientTwo.get();

        assertTrue(writeLockAcquiredClientOne);
        assertFalse(writeLockAcquiredClientTwo);

        executorService.shutdown();
    }

    @Test
    public void testReadWriteLockConflictSameClient() throws RemoteException {

        // Scenario: When same client tries to get the write lock after getting read
        // lock for same element

        // First client acquires a read lock
        boolean readLockAcquired = remoteArray.requestReadLock(1, 123);
        // Verify that the first client acquired the read lock
        assertTrue(readLockAcquired);

        // Second client acquires a write lock for the same position
        boolean writeLockAcquired = remoteArray.requestWriteLock(1, 123);
        // Verify that the second client fails to acquire the write lock
        assertFalse(writeLockAcquired);
    }

    @Test
    public void testReadWriteLockConflictDifferentClient()
            throws RemoteException, ExecutionException, InterruptedException {

        // Scenario: When second client tries to acquire write lock where read lock is already given to another client

        ExecutorService executorService = Executors.newFixedThreadPool(2); // Two threads for two clients

        // First client acquires a read lock
        Future<Boolean> readLockResult = executorService.submit(() -> remoteArray.requestReadLock(1, 123));

        // Second client acquires a write lock for the same position
        Future<Boolean> writeLockResult = executorService.submit(() -> remoteArray.requestWriteLock(1, 456));

        // Get the results from the threads
        boolean writeLockAcquired = writeLockResult.get();
        boolean readLockAcquired = readLockResult.get();

        // Verify that the first client acquired the read lock
        assertTrue(readLockAcquired);
        // Verify that the second client fails to acquire the write lock
        assertFalse(writeLockAcquired);

        executorService.shutdown();

    }

    @Test
    public void testWriteReadLockConflict() throws RemoteException, InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2); // Two threads for two clients

        // First client acquires a write lock
        Future<Boolean> writeLockResult = executorService.submit(() -> remoteArray.requestWriteLock(4, 456));

        // Second client acquires a read lock for the same position
        Future<Boolean> readLockResult = executorService.submit(() -> remoteArray.requestReadLock(4, 123));

        // Get the results from the threads
        boolean writeLockAcquired = writeLockResult.get();
        boolean readLockAcquired = readLockResult.get();

        // Verify that the first client acquired the write lock
        assertTrue(writeLockAcquired);
        // Verify that the second client failed to acquire the read lock due to the
        // write lock
        assertFalse(readLockAcquired);

        executorService.shutdown();
    }
}
