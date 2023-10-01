package rmiproject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RemoteStringArray extends UnicastRemoteObject implements RemoteStringArrayInterface {

    private static final Logger logger = Logger.getLogger(RemoteStringArray.class.getName());

    private List<ArrayItem> array;

    public RemoteStringArray(int capacity) throws RemoteException {
        array = new ArrayList<ArrayItem>(capacity);
    }

    @Override
    public void insertArrayElement(int l, String str) throws RemoteException {
        array.add(l, new ArrayItem(str));
    }

    @Override
    public boolean requestReadLock(int l, int clientId) throws RemoteException {
        ArrayItem item = array.get(l);
        if (item.getReadLockHolderId() == clientId || item.getReadLockHolderId() == 0) {
            // TODO: Make sure it returns True even if the currently lock is held by same
            // client id
            // TODO: Check if multiple clients can have read lock to same item
            return item.getRwLock().readLock().tryLock();
        } else {
            logger.warning(String.format("Access not granted as read lock for %d th element hold by %d", l,
                    item.getReadLockHolderId()));
        }

        return false;
    }

}
