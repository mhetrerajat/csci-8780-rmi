package rmiproject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Logger;

public class RMIClientMain {

    private static final Logger logger = Logger.getLogger(RMIClientMain.class.getName());

    public static void main(String[] args) throws RemoteException, NotBoundException {

        // Get configuration values using ConfigReader
        String serverHost = ConfigReader.getServerHost();
        Integer serverPort = ConfigReader.getServerPort();
        String bindName = ConfigReader.getRemoteObjectBindName();

        // Get the registry
        Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);

        // Lookup the remote object from the registry
        RMIInterface remoteObject = (RMIInterface) registry.lookup(bindName);

        // Create a Scanner for user input
        Scanner scanner = new Scanner(System.in);
        int choice;

        // Interactive CLI loop
        do {
            System.out.println("Choose an option:");
            System.out.println("1. getArrayCapacity");
            System.out.println("2. fetchElementRead");
            System.out.println("3. fetchElementWrite");
            System.out.println("4. printElement");
            System.out.println("5. concatenate");
            System.out.println("6. writeback");
            System.out.println("7. releaseLock");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    displayArrayCapacity();
                    break;
                case 2:
                    fetchElementRead();
                    break;
                case 3:
                    fetchElementWrite();
                    break;
                case 4:
                    printElement();
                    break;
                case 5:
                    concatenate();
                    break;
                case 6:
                    writeback();
                    break;
                case 7:
                    releaseLock();
                    break;
                case 9:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 9);

        scanner.close();

    }

    private static void displayArrayCapacity() {
        // Replace this with the logic to get the array capacity from the server
        throw new UnsupportedOperationException("Get_Array_Capacity not implemented");
    }

    private static void fetchElementRead() {
        // Replace this with the logic to fetch an element in read-only mode
        throw new UnsupportedOperationException("Fetch_Element_Read not implemented");
    }

    private static void fetchElementWrite() {
        // Replace this with the logic to fetch an element in read-write mode
        throw new UnsupportedOperationException("Fetch_Element_Write not implemented");
    }

    private static void printElement() {
        // Replace this with the logic to print an element
        throw new UnsupportedOperationException("Print_Element not implemented");
    }

    private static void concatenate() {
        // Replace this with the logic to concatenate a string to an element
        throw new UnsupportedOperationException("Concatenate not implemented");
    }

    private static void writeback() {
        // Replace this with the logic to write back an element to the server
        throw new UnsupportedOperationException("Writeback not implemented");
    }

    private static void releaseLock() {
        // Replace this with the logic to release a lock on an element
        throw new UnsupportedOperationException("Release_Lock not implemented");
    }

}
