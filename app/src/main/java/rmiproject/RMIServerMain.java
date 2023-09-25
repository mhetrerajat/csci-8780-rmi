package rmiproject;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerMain {
    public static void main(String[] args) {
        try {
            // Create an instance of the remote object
            RMIInterfaceImpl remoteObject = new RMIInterfaceImpl();

            // Export the remote object
            RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(remoteObject, 0);

            // Get a registry
            Registry registry = LocateRegistry.getRegistry();

            // Bind the remote object to the registry
            registry.rebind("RMIExample", stub);

            System.out.println("Server is ready!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
