package rmiproject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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

    public RemoteStringArrayImpl(int capacity) throws RemoteException {
        array = new ArrayList<String>(capacity);
    }

    @Override
    public void insertArrayElement(int l, String str) throws RemoteException {
        // NOTE: Assumption; This method is only used by server
        array.add(l, str);
    }

    @Override
    public boolean requestReadLock(int l, int clientId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requestReadLock'");
    }

    @Override
    public boolean requestWriteLock(int l, int clientId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requestWriteLock'");
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
