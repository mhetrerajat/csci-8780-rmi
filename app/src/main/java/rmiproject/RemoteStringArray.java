package rmiproject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

public interface RemoteStringArray extends Remote {
    public void insertArrayElement(int l, String str) throws RemoteException;

    public boolean requestReadLock(int l, int clientId) throws RemoteException;

    public boolean requestWriteLock(int l, int clientId) throws RemoteException;

    public void releaseLock(int l, int clientId) throws RemoteException;

    public String fetchElementRead(int l, int client_id) throws RemoteException;

    public String fetchElementWrite(int l, int client_id) throws RemoteException;

    public boolean WriteBackElement(String str, int l, int client_id) throws RemoteException;

    // custom methods for client
    public int getClientId();

    public Integer getRemoteArrayCapacity();
}
