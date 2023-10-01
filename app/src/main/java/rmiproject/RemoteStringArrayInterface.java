package rmiproject;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteStringArrayInterface extends Remote {
    // inserts str as the lth element of the string array. You can assume that l <
    // capacity of the String array.
    public void insertArrayElement(int l, String str) throws RemoteException;

    public boolean requestReadLock(int l, int clientId) throws RemoteException;

//    public boolean requestWriteLock(int l, int clientId) throws RemoteException;
//
//    public void releaseLock(int l, int clientId) throws RemoteException;

}
