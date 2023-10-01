package rmiproject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
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

            // Create a Scanner for user input
            Scanner scanner = new Scanner(System.in);

            // Interactive CLI loop
            while (true) {
                System.out.println("1. Say Hello");
                System.out.println("2. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        String result = remoteObject.sayHello();
                        System.out.println("Remote method result: " + result);
                        break;
                    case 2:
                        System.out.println("Exiting...");
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

}
