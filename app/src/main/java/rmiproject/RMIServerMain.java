package rmiproject;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.logging.Logger;

public class RMIServerMain implements RMIInterface, Unreferenced {

    private static final Logger logger = Logger.getLogger(RMIServerMain.class.getName());

    // Export remote objects once during initialization in the constructor, not
    // within methods called multiple times.
    public RMIServerMain() throws RemoteException {
        super();

        // Get configuration values using ConfigReader
        Integer serverPort = ConfigReader.getServerPort();
        String bindName = ConfigReader.getRemoteObjectBindName();

        // Export the remote object
        RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(this, 0);

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
            UnicastRemoteObject.unexportObject(this, true);
            logger.info("Server object unexported.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello !!";
    }

}
