package rmiproject;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ArrayItem {
    private String value = "";
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private ConcurrentSkipListSet<Integer> readers;
    private volatile Integer writer;

    public ConcurrentSkipListSet<Integer> getReaders() {
        return readers;
    }

    public void markAsReader(Integer clientId) {
        readers.add(clientId);
    }
    public void removeAsReader(Integer clientId) {readers.remove(clientId);}

    public Integer getWriterClientId() {
        return writer;
    }

    public void setWriter(Integer clientId) {
        this.writer = clientId;
    }

    public boolean isWriteLockAvailableForClientId(int clientId){
        return writer == null || writer.equals(clientId);
    }

    public void removeAsWriter(Integer clientId) {this.setWriter(null);}

    public ArrayItem() {
        value = "";
        readers = new ConcurrentSkipListSet<>();
    }

    public ArrayItem(String value) {
        value = value;
        readers = new ConcurrentSkipListSet<>();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ReadWriteLock getRwLock() {
        return rwLock;
    }

    public void setRwLock(ReadWriteLock rwLock) {
        this.rwLock = rwLock;
    }

    @Override
    public String toString() {
        return "ArrayItem [value=" + value + ", rwLock=" + rwLock + "]";
    }

}
