package rmiproject;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteStringArrayImpl implements RemoteStringArray{

    @Override
    public void insertArrayElement(int l, String str) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertArrayElement'");
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
    public AtomicInteger getClientId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getClientId'");
    }

    @Override
    public Integer getRemoteArrayCapacity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRemoteArrayCapacity'");
    }
    
}
