package rmiproject;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RemoteStringArrayImpl implements RemoteStringArray {

    private static final Logger logger = Logger.getLogger(RemoteStringArrayImpl.class.getName());

    private CopyOnWriteArrayList<ArrayItem> array;
    private AtomicInteger clientCounter;

    public RemoteStringArrayImpl(int capacity) throws RemoteException {
        array = Stream.generate(() -> new ArrayItem()).limit(capacity)
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        clientCounter = new AtomicInteger(0);

        // Start a separate thread to periodically check and release locks
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::checkAndReleaseLocks, 0, 5000,
                TimeUnit.MILLISECONDS);
    }

    private void checkAndReleaseLocks() {
        long currentTime = System.currentTimeMillis();

        // Release locks for the reader
        IntStream.range(0, array.size())
                .forEach(index -> {
                    ArrayItem item = array.get(index);
                    List<Integer> staleReaders = item.getStaleReaders(currentTime);
                    staleReaders.forEach(reader -> {
                        releaseReadLock(index, reader); // release locks
                        logger.warning(
                                String.format("Auto-released stale read lock of Client[%d] for array element %d",
                                        reader, index));
                    });

                    // writers
                    Boolean isStaleWriter = item.isStaleWriter(currentTime);
                    if (isStaleWriter) {
                        Integer writerId = item.getWriterId();
                        releaseWriteLock(index, writerId); // release locks
                        logger.warning(
                                String.format("Auto-released stale write lock of Client[%d] for array element %d",
                                        writerId, index));
                    }
                });

    }

    @Override
    public void insertArrayElement(int l, String str) throws RemoteException {
        // NOTE: Assumption; This method is only used by server
        ArrayItem newItem = new ArrayItem(str);
        array.set(l, newItem);
    }

    @Override
    public boolean requestReadLock(int l, int clientId) throws RemoteException {
        ArrayItem item = array.get(l);
        boolean lockAcquired = false;
        try {
            // get the read lock if there are no writers
            // if the client has write lock then it also has read permission
            // if client already has the read lock
            if (!item.hasWriteLock() || item.doesClientHaveWriteLock(clientId)
                    || item.doesClientHasReadLock(clientId)) {
                lockAcquired = true;
            }
        } finally {
            if (lockAcquired) {
                // register client as reader if have the read lock and mark timestamp
                item.markAsReader(clientId, System.currentTimeMillis());
            }
        }
        return lockAcquired;
    }

    @Override
    public boolean requestWriteLock(int l, int clientId) throws RemoteException {
        ArrayItem item = array.get(l);
        boolean lockAcquired = false;
        try {

            // if there no read locks already given
            // if the client already have the write lock
            if (!item.hasReadLocks() || item.doesClientHaveWriteLock(clientId)) {
                lockAcquired = true;
            }
        } finally {
            if (lockAcquired) {
                // Check if the write lock is already given to any other client
                Integer currentWriter = item.getWriterId();
                if (currentWriter != null && !currentWriter.equals(clientId)) {
                    // Another client already has the write lock
                    return false;
                }

                // mark as write with timestamp
                item.markAsWriter(clientId, System.currentTimeMillis());
            }
        }

        return lockAcquired;
    }

    private void releaseReadLock(int l, int clientId) {
        ArrayItem item = array.get(l);
        if (item.hasReadLocks()) {
            // Check if the client has held the lock and if so remove it
            if (item.doesClientHasReadLock(clientId)) {
                item.removeReader(clientId); // remove the client as reader
            }
        }
    }

    private void releaseWriteLock(int l, int clientId) {
        ArrayItem item = array.get(l);
        if (item.hasWriteLock()) {
            // check if this clientId has held the write lock and if so remove it
            if (item.doesClientHaveWriteLock(clientId)) {
                item.removeWriter(); // unlock write
            }
        }
    }

    @Override
    public void releaseLock(int l, int clientId) throws RemoteException {
        releaseReadLock(l, clientId);
        releaseWriteLock(l, clientId);
    }

    @Override
    public String fetchElementRead(int l, int clientId) throws RemoteException {
        return requestReadLock(l, clientId) ? array.get(l).getValue() : null;
    }

    @Override
    public String fetchElementWrite(int l, int clientId) throws RemoteException {
        return requestWriteLock(l, clientId) ? array.get(l).getValue() : null;
    }

    @Override
    public boolean WriteBackElement(String str, int l, int clientId) throws RemoteException {
        ArrayItem item = array.get(l);

        // check if client already has the write lock
        if (item.hasWriteLock() && item.doesClientHaveWriteLock(clientId)) {
            // set value in array
            item.setValue(str);
            return true;
        }
        return false;
    }

    @Override
    public int getClientId() throws RemoteException {
        return clientCounter.incrementAndGet();
    }

    @Override
    public Integer getRemoteArrayCapacity() throws RemoteException {
        return array.size();
    }

    @Override
    public Map<String, List<Integer>> getCurrentLocksHoldByClient(Integer clientId) throws RemoteException {
        List<Integer> reads = IntStream.range(0, array.size())
                .filter(i -> array.get(i).doesClientHasReadLock(clientId))
                .boxed()
                .toList();

        List<Integer> writes = IntStream.range(0, array.size())
                .filter(i -> array.get(i).doesClientHaveWriteLock(clientId))
                .boxed()
                .toList();

        Map<String, List<Integer>> lockInfo = new HashMap<>();
        lockInfo.put("read", reads);
        lockInfo.put("write", writes);

        return lockInfo;
    }

}
