package rmiproject;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteStringArrayInterface extends Remote {
    // inserts str as the lth element of the string array. You can assume that l <
    // capacity of the String array.
    public void insertArrayElement(int l, String str) throws RemoteException;

    // Client Specific Methods
    public int getArrayCapacity() throws RemoteException;

    // Print the string associated with the ith element.
    public void printElement(int i) throws RemoteException;

    // Concatenate Str to the contents of the ith element.
    public void concatenate(int i, String str) throws RemoteException;

}
