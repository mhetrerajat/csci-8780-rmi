package rmiproject;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {
    String sayHello() throws RemoteException;
    String sendMessage(String message) throws RemoteException;
}
