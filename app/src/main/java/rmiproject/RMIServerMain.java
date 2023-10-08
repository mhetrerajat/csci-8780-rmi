package rmiproject;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.List;
import java.util.logging.Logger;

public class RMIServerMain implements Unreferenced {

    private static final Logger logger = Logger.getLogger(RMIServerMain.class.getName());

    private static final String DEFAULT_SERVER_CONFIG_PATH = "/server-config.properties";

    private static String configPath;

    private ConfigReader configReader;

    private RemoteStringArrayImpl remoteArray;

    // Export remote objects once during initialization in the constructor, not
    // within methods called multiple times.
    public RMIServerMain() {
        super();
    }

    private void init(String configPath) throws RemoteException {
        configReader = new ConfigReader(configPath);

        // Get configuration values using ConfigReader
        Integer serverPort = configReader.getServerPort();
        String bindName = configReader.getRemoteObjectBindName();
        Integer arrayCapacity = configReader.getRemoteArrayCapacity();
        List<String> initArray = configReader.getRemoteArrayInitValue();
        long lockAutoReleaseTimeout = configReader.getLockAutoReleaseTimeout();

        // Create an instance of the remote object
        remoteArray = new RemoteStringArrayImpl(arrayCapacity, lockAutoReleaseTimeout);

        // Initialize the array with the provided strings
        for (int i = 0; i < initArray.size(); i++) {
            remoteArray.insertArrayElement(i, initArray.get(i));
        }

        // Export the remote object
        RemoteStringArray stub = (RemoteStringArray) UnicastRemoteObject.exportObject(remoteArray, 0);

        // Get a registry
        Registry registry = LocateRegistry.createRegistry(serverPort);

        // Bind the remote object to the registry
        registry.rebind(bindName, stub);

        logger.info("Server is ready!");
    }


    public static void main(String[] args) {
        try {
            // init
            RMIServerMain rmiServer = new RMIServerMain();
            configPath = ConfigReader.parseConfigPathFromCLI(args, DEFAULT_SERVER_CONFIG_PATH);
            rmiServer.init(configPath);
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
