package rmiproject;

import java.rmi.RemoteException;

public class RemoteStringArray implements RemoteStringArrayInterface {

    private String[] array;

    public RemoteStringArray(int capacity) throws RemoteException {
        array = new String[capacity];
    }

    @Override
    public void insertArrayElement(int l, String str) throws RemoteException {
        array[l] = str;
    }

    @Override
    public int getArrayCapacity() throws RemoteException {
        return array.length;
    }

}
