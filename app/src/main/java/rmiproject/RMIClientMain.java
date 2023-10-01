package rmiproject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class RMIClientMain {

    private static final Logger logger = Logger.getLogger(RMIClientMain.class.getName());

    public static void main(String[] args) {

        // Get configuration values using ConfigReader
        String serverHost = ConfigReader.getServerHost();
        Integer serverPort = ConfigReader.getServerPort();
        String bindName = ConfigReader.getRemoteObjectBindName();

        try {
            // Get the registry
            Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);

            // Lookup the remote object from the registry
            RMIInterface remoteObject = (RMIInterface) registry.lookup(bindName);

            // Call remote methods here, passing the message as an argument
            String response = remoteObject.sayHello();
            logger.info("Response from server: " + response);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
