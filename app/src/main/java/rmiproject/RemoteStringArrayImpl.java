package rmiproject;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
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

    private ArrayList<ArrayItem> array;
    private AtomicInteger clientCounter;

    public RemoteStringArrayImpl(int capacity) throws RemoteException {
        array = Stream.generate(() -> new ArrayItem()).limit(capacity)
                .collect(Collectors.toCollection(ArrayList::new));
        clientCounter = new AtomicInteger(0);
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
            // try to acquire lock
            lockAcquired = item.tryReadLock();
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
            lockAcquired = item.tryWriteLock();
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
                item.readUnlock();
                item.removeReader(clientId); // remove the client as reader
            }
        }
    }

    private void releaseWriteLock(int l, int clientId) {
        ArrayItem item = array.get(l);
        if (item.hasWriteLock()) {
            // check if this clientId has held the write lock and if so remove it
            if (item.doesClientHaveWriteLock(clientId)) {
                item.writeUnlock();
                item.removeWriter();
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

}
