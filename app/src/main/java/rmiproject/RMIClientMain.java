package rmiproject;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class RMIClientMain {

    private static final Logger logger = Logger.getLogger(RMIClientMain.class.getName());

    public static void main(String[] args) {
        if (args.length != 1) {
            logger.severe("Usage: RMIClientMain <message>");
            System.exit(1);
        }

        String message = args[0];

        try {
            // Get the registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Lookup the remote object from the registry
            RMIInterface remoteObject = (RMIInterface) registry.lookup("RMIExample");

            // Call remote methods here, passing the message as an argument
            String response = remoteObject.sayHello();
            logger.info("Response from server: " + response);

            response = remoteObject.sendMessage(message);
            logger.info("Server Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
