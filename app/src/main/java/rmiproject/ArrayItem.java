package rmiproject;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayItem {
    private String value;
    private ConcurrentHashMap<Integer, Long> readers;
    private Integer writer;
    private Long writeLockTimestamp;
    private ReentrantReadWriteLock rwLock;

    private final long lockTimeout = 60000; // 60 seconds

    public ArrayItem() {
        this.value = "";
        this.readers = new ConcurrentHashMap<Integer, Long>();
        this.rwLock = new ReentrantReadWriteLock();
    }

    public ArrayItem(String value) {
        this.value = value;
        this.readers = new ConcurrentHashMap<>();
        this.rwLock = new ReentrantReadWriteLock();
    }

    // Value
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // Locks
    public ReentrantReadWriteLock getRwLock() {
        return rwLock;
    }

    public Boolean hasReadLocks() {
        return rwLock.getReadLockCount() > 0;
    }

    public void readUnlock() {
        rwLock.readLock().unlock();
    }

    // Readers
    public void markAsReader(Integer clientId, Long timestamp) {
        readers.put(clientId, timestamp);
    }

    public void removeReader(Integer clientId) {
        readers.remove(clientId);
    }

    public Boolean doesClientHasReadLock(Integer clientId) {
        return readers.containsKey(clientId);
    }

    public List<Integer> getStaleReaders(long currentTime) {
        return readers.entrySet().stream()
                .filter(entry -> entry.getValue() != null && (currentTime - entry.getValue()) > lockTimeout)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    public boolean tryReadLock() {
        return rwLock.readLock().tryLock();
    }

    // Writer
    public Integer getWriterId() {
        return writer;
    }

    public void markAsWriter(Integer clientId, Long timestamp) {
        writer = clientId;
        writeLockTimestamp = timestamp;
    }

    public boolean hasWriteLock() {
        return rwLock.isWriteLocked();
    }

    public boolean doesClientHaveWriteLock(int clientId) {
        return writer == clientId;
    }

    public void writeUnlock() {
        rwLock.writeLock().unlock();
    }

    public void removeWriter() {
        writer = null;
        writeLockTimestamp = null;
    }

    public boolean isStaleWriter(long currentTime) {
        return (currentTime - writeLockTimestamp) > lockTimeout;
    }

    public boolean tryWriteLock() {
        return rwLock.writeLock().tryLock();
    }

}
