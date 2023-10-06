package rmiproject;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RemoteStringArrayImpl implements RemoteStringArray {

    private static final Logger logger = Logger.getLogger(RemoteStringArrayImpl.class.getName());

    private ArrayList<String> array;
    private AtomicInteger clientCounter;
    private ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> readers;
    private ConcurrentHashMap<Integer, Integer> writers;
    private ReentrantReadWriteLock[] locks;

    public RemoteStringArrayImpl(int capacity) throws RemoteException {
        array = Stream.generate(() -> "").limit(capacity)
                .collect(Collectors.toCollection(ArrayList::new));
        clientCounter = new AtomicInteger(0);
        readers = new ConcurrentHashMap<>(capacity);
        writers = new ConcurrentHashMap<>(capacity);
        locks = IntStream.range(0, capacity)
                .mapToObj(i -> new ReentrantReadWriteLock(true))
                .toArray(ReentrantReadWriteLock[]::new);
    }

    @Override
    public void insertArrayElement(int l, String str) throws RemoteException {
        // NOTE: Assumption; This method is only used by server
        array.set(l, str);
    }

    @Override
    public boolean requestReadLock(int l, int clientId) throws RemoteException {
        boolean lockAcquired = false;
        try {
            // try to acquire lock
            lockAcquired = locks[l].readLock().tryLock();
        } finally {
            if (lockAcquired) {
                // register client as reader if have the read lock
                readers.computeIfAbsent(l, key -> new CopyOnWriteArrayList<>()).addIfAbsent(clientId);
            }
        }
        return lockAcquired;
    }

    @Override
    public boolean requestWriteLock(int l, int clientId) throws RemoteException {
        boolean lockAcquired = false;
        try {
            lockAcquired = locks[l].writeLock().tryLock();
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
        ReentrantReadWriteLock lock = locks[l];

        // check if it has the read lock
        if (lock.getReadLockCount() > 0) {
            // check if the this clientId has held the lock and if so remove it
            readers.computeIfPresent(l, (key, clients) -> {
                boolean isRemoved = clients.removeIf(reader -> reader == clientId);
                // release the actual read locks
                if (isRemoved) {
                    lock.readLock().unlock();
                }
                return clients.isEmpty() ? null : clients;
            });
        }

        // check if it has the client lock
        if (lock.isWriteLocked()) {
            // check if this clientId has held the write lock and if so remove it
            writers.computeIfPresent(l, (key, writerId) -> {
                if (writerId == clientId) {
                    // release the actual write lock
                    lock.writeLock().unlock();
                    return null;
                }
                return writerId;
            });

        }
    }

    @Override
    public String fetchElementRead(int l, int clientId) throws RemoteException {
        return requestReadLock(l, clientId) ? array.get(l) : null;
    }

    @Override
    public String fetchElementWrite(int l, int clientId) throws RemoteException {
        return requestWriteLock(l, clientId) ? array.get(l) : null;
    }

    @Override
    public boolean WriteBackElement(String str, int l, int clientId) throws RemoteException {
        ReentrantReadWriteLock lock = locks[l];
        boolean isClientWriteLock = writers.contains(clientId);

        // check if client already has the write lock
        if (lock.isWriteLocked() && isClientWriteLock) {
            // set value in array
            array.set(l, str);
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
