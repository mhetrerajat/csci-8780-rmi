package rmiproject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class RemoteStringArray extends UnicastRemoteObject implements RemoteStringArrayInterface {

    private static final Logger logger = Logger.getLogger(RemoteStringArray.class.getName());

    private String[] array;

    public RemoteStringArray(int capacity) throws RemoteException {
        array = new String[capacity];
    }

    @Override
    public void insertArrayElement(int l, String str) throws RemoteException {
        array[l] = str;
    }

}
