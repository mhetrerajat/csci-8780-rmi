package rmiproject;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.logging.Logger;

public class RMIServerMain implements Unreferenced {

    private static final Logger logger = Logger.getLogger(RMIServerMain.class.getName());

    private RemoteStringArray remoteArray;

    // Export remote objects once during initialization in the constructor, not
    // within methods called multiple times.
    public RMIServerMain() throws RemoteException {
        super();

        // Get configuration values using ConfigReader
        Integer serverPort = ConfigReader.getServerPort();
        String bindName = ConfigReader.getRemoteObjectBindName();

        // Create an instance of the remote object
        // TODO: Pass the array capacity from the arguments
        remoteArray = new RemoteStringArray(5);

        // TODO: Take list of strings as argument from CLI to initialize the array
        String[] initArray = { "a", "b", "c" };
        for (int i = 0; i < initArray.length; i++) {
            remoteArray.insertArrayElement(i, initArray[i]);
        }

        // Export the remote object
        RemoteStringArrayInterface stub = (RemoteStringArrayInterface) UnicastRemoteObject.exportObject(remoteArray,
                0);

        // Get a registry
        Registry registry = LocateRegistry.createRegistry(serverPort);

        // Bind the remote object to the registry
        registry.rebind(bindName, stub);

        logger.info("Server is ready!");
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
        logger.warning("Server is unreferenced. Shutting down gracefully...");

        try {
            UnicastRemoteObject.unexportObject(remoteArray, true);
            logger.info("Server object unexported.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
