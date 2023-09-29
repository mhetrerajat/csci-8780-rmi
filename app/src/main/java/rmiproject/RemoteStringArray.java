package rmiproject;

import java.rmi.RemoteException;
import java.util.logging.Logger;

public class RemoteStringArray implements RemoteStringArrayInterface {

    private static final Logger logger = Logger.getLogger(RemoteStringArray.class.getName());

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

    @Override
    public void printElement(int i) throws RemoteException {
        if (i >= 0 && i < array.length) {
            logger.info(String.format("printElement(%d) : %s", i, array[i]));
        } else {
            throw new RemoteException("Invalid index");
        }
    }

    @Override
    public void concatenate(int i, String str) throws RemoteException {
        if (i >= 0 && i < array.length) {
            array[i] += str;
            logger.info(String.format("New Concatenated String : %s", array[i]));
        } else {
            throw new RemoteException("Invalid index");
        }
    }

}
