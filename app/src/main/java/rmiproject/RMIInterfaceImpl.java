package rmiproject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIInterfaceImpl extends UnicastRemoteObject implements RMIInterface {
    public RMIInterfaceImpl() throws RemoteException {
        super();
    }

    public String sayHello() throws RemoteException {
        return "Hello !!";
    }

    @Override
    public String sendMessage(String message) throws RemoteException {
        // Implement the remote method logic here
        return "Received message: " + message;
    }
}
