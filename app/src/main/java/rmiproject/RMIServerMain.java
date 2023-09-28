package rmiproject;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

public class RMIServerMain implements RMIInterface, Unreferenced {

    // Export remote objects once during initialization in the constructor, not
    // within methods called multiple times.
    public RMIServerMain() throws RemoteException {
        super();

        // Export the remote object
        RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(this, 0);

        // Get a registry
        Registry registry = LocateRegistry.createRegistry(1099);

        // Bind the remote object to the registry
        registry.rebind("RMIExample", stub);

        System.out.println("Server is ready!");
    }

    public static void main(String[] args) {
        try {
            new RMIServerMain();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unreferenced() {
        System.out.println("Server is unreferenced. Shutting down gracefully...");

        try {
            UnicastRemoteObject.unexportObject(this, true);
            System.out.println("Server object unexported.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello !!";
    }

    @Override
    public String sendMessage(String message) throws RemoteException {
        return "Received message: " + message;
    }
}
