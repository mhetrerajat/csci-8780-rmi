package rmiproject;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClientMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: RMIClientMain <message>");
            System.exit(1);
        }

        String message = args[0];

        try {
            // Get the registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Lookup the remote object from the registry
            RMIInterface remoteObject = (RMIInterface) registry.lookup("RMIExample");

            // Call remote methods here, passing the message as an argument
            String response = remoteObject.sendMessage(message);
            System.out.println("Server Response: " + response);
            // String response = remoteObject.sayHello();
            // System.out.println("Response from server: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
