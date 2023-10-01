package rmiproject;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ArrayItem {
    private String value = "";
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ArrayItem() {
        this.value = "";
    }

    public ArrayItem(String value) {
        this.value = value;
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
