package rmiproject;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ArrayItem {
    private String value;
    // clientId -> timestamp
    private ConcurrentHashMap<Integer, Long> readers;
    private Integer writer;
    private Long writeLockTimestamp;

    private long lockTimeout;

    public ArrayItem(long lockTimeout) {
        this.value = "";
        this.readers = new ConcurrentHashMap<Integer, Long>();
        this.lockTimeout = lockTimeout;
    }

    public ArrayItem(String value, long lockTimeout) {
        this.value = value;
        this.readers = new ConcurrentHashMap<>();
        this.lockTimeout = lockTimeout;
    }

    // Value
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // Locks

    public Boolean hasReadLocks() {
        return readers.size() > 0;
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

    // Writer
    public Integer getWriterId() {
        return writer;
    }

    public void markAsWriter(Integer clientId, Long timestamp) {
        writer = clientId;
        writeLockTimestamp = timestamp;
    }

    public boolean hasWriteLock() {
        return writer != null;
    }

    public boolean doesClientHaveWriteLock(int clientId) {
        return writer != null && writer == clientId;
    }

    public void removeWriter() {
        writer = null;
        writeLockTimestamp = null;
    }

    public boolean isStaleWriter(long currentTime) {
        return writeLockTimestamp != null && (currentTime - writeLockTimestamp) > lockTimeout;
    }

}
