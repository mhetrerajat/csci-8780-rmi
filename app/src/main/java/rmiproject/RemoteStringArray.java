package rmiproject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
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

        boolean lockAcquired = item.getRwLock().readLock().tryLock();
        if(lockAcquired) {
            item.markAsReader(clientId);
        }
        logger.warning(String.format("Access request for read lock for %d th element: %b", l,
                    lockAcquired));

        return lockAcquired;
    }

//    @Override
//    public boolean requestWriteLock(int l, int clientId) throws RemoteException {
//        ArrayItem item = array.get(l);
//        if (item.getWriteLockHolderId() == clientId || item.getWriteLockHolderId() == 0) {
//            // TODO: Make sure it returns True even if the currently lock is held by same
//            // client id
//            // TODO: Check if multiple clients can have read lock to same item
//            return item.getRwLock().writeLock().tryLock();
//        } else {
//            logger.warning(String.format("Access not granted as read lock for %d th element hold by %d", l,
//                    item.getWriteLockHolderId()));
//        }
//
//        return false;
//    }
//
//    @Override
//    public void releaseLock(int l, int clientId) throws RemoteException {
//        ArrayItem item = array.get(l);
//
//        ReadWriteLock lock = item.getRwLock();
//        Lock readLock = lock.readLock();
//        Lock writeLock = lock.writeLock();
//
//        // release read locks
//        if (item.getReadLockHolderId() == clientId && readLock != null) {
//            readLock.unlock();
//            item.setReadLockHolderId(0);
//        }
//
//        // release write locks
//        if (item.getWriteLockHolderId() == clientId && writeLock != null) {
//            writeLock.unlock();
//            item.setWriteLockHolderId(0);
//        }
//
//        // TODO: Possibly need to update the original array with updated arrayItem
//    }

}
