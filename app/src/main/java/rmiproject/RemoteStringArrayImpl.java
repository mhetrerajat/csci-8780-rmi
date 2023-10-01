package rmiproject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class RemoteStringArrayImpl extends UnicastRemoteObject implements RemoteStringArray {

    private static final Logger logger = Logger.getLogger(RemoteStringArrayImpl.class.getName());

    private ArrayList<String> array;
    private AtomicInteger clientCounter;
    private ConcurrentHashMap<Integer, List<Integer>> readers;
    private ConcurrentHashMap<Integer, Integer> writers;
    private ReentrantReadWriteLock[] locks;

    private final Integer LOCK_TIMEOUT = 600;
    private final TimeUnit LOCK_TIME_UNIT = TimeUnit.SECONDS;

    public RemoteStringArrayImpl(int capacity) throws RemoteException {
        array = new ArrayList<String>(capacity);
        clientCounter = new AtomicInteger(0);
        readers = new ConcurrentHashMap<>(capacity);
        writers = new ConcurrentHashMap<>(capacity);
        locks = new ReentrantReadWriteLock[capacity];
    }

    @Override
    public void insertArrayElement(int l, String str) throws RemoteException {
        // NOTE: Assumption; This method is only used by server
        array.add(l, str);
    }

    @Override
    public boolean requestReadLock(int l, int clientId) throws RemoteException {
        boolean lockAcquired = false;
        try {
            // try to acquire lock
            lockAcquired = locks[l].readLock().tryLock(LOCK_TIMEOUT, LOCK_TIME_UNIT);
        } catch (InterruptedException e) {
            logger.severe(String.format("Interrupt Exception"));
        } finally {
            if (lockAcquired) {
                // register client as reader if have the read lock
                readers.computeIfAbsent(l, key -> new CopyOnWriteArrayList<>()).add(clientId);
            }
        }
        return lockAcquired;
    }

    @Override
    public boolean requestWriteLock(int l, int clientId) throws RemoteException {
        boolean lockAcquired = false;
        try {
            lockAcquired = locks[l].writeLock().tryLock(LOCK_TIMEOUT, LOCK_TIME_UNIT);
        } catch (InterruptedException e) {
            logger.severe(String.format("Interrupt Exception"));
        } finally {
            if (lockAcquired) {
                // Check if the write lock is already given to any other client
                Integer currentWriter = writers.get(l);
                if (currentWriter != null && !currentWriter.equals(clientId)) {
                    // Another client already has the write lock
                    return false;
                }

                // Update the writers map with the new writer
                writers.put(l, clientId);
            }
        }

        return lockAcquired;
    }

    @Override
    public void releaseLock(int l, int clientId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'releaseLock'");
    }

    @Override
    public String fetchElementRead(int l, int client_id) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fetchElementRead'");
    }

    @Override
    public String fetchElementWrite(int l, int client_id) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fetchElementWrite'");
    }

    @Override
    public boolean WriteBackElement(String str, int l, int client_id) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'WriteBackElement'");
    }

    @Override
    public int getClientId() {
        return clientCounter.incrementAndGet();
    }

    @Override
    public Integer getRemoteArrayCapacity() {
        return array.size();
    }

}
