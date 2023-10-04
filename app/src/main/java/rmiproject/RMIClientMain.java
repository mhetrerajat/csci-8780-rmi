package rmiproject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

public class RMIClientMain {

    private static final Logger logger = Logger.getLogger(RMIClientMain.class.getName());

    // client id assigned by the server
    private Integer clientId;

    // Local copy of the remote array
    private ArrayList<String> localArray;

    public RMIClientMain() throws RemoteException, NotBoundException {
        // Get configuration values using ConfigReader
        String serverHost = ConfigReader.getServerHost();
        Integer serverPort = ConfigReader.getServerPort();
        String bindName = ConfigReader.getRemoteObjectBindName();

        // Get the registry
        Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);

        // Lookup the remote object from the registry
        RemoteStringArray remoteObject = (RemoteStringArray) registry.lookup(bindName);

        // init local copy of the array same size as of remote array
        localArray = new ArrayList<>(remoteObject.getRemoteArrayCapacity());
        // get the client id from the server
        clientId = remoteObject.getClientId();

    }

    public static void main(String[] args) {

        // TODO: Automatically release the locks if the client gets killed / crashed

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

        // TODO: Fetch the maximum array capacity from the server.
        // Should return the max possible capacity not the current length of the array

        throw new UnsupportedOperationException("Get_Array_Capacity not implemented");
    }

    private static void fetchElementRead() {
        // Replace this with the logic to fetch an element in read-only mode

        // TODO: Call the fetchElementRead from the server
        // Copy the fetched value into the local copy of the array specific to this
        // client
        // Only print "success" or "failure" based on the status of the operation

        throw new UnsupportedOperationException("Fetch_Element_Read not implemented");
    }

    private static void fetchElementWrite() {
        // Replace this with the logic to fetch an element in read-write mode

        // TODO: Call the fetchElementWrite from the server
        // Copy the fetched value into the local copy of the array specific to this
        // client
        // Only print "success" or "failure" based on the status of the operation
        throw new UnsupportedOperationException("Fetch_Element_Write not implemented");
    }

    private static void printElement() {
        // Replace this with the logic to print an element

        // TODO: Print the current value of the element in the local copy of the array
        // No need to fetch it from the server, assume that element already being
        // fetched from the server

        throw new UnsupportedOperationException("Print_Element not implemented");
    }

    private static void concatenate() {
        // Replace this with the logic to concatenate a string to an element

        // TODO: Modify the current value of the element in the local copy of the array
        // by concating the string
        // Assume that element already being fetched from the server
        // do not write back to remote array

        throw new UnsupportedOperationException("Concatenate not implemented");
    }

    private static void writeback() {
        // Replace this with the logic to write back an element to the server

        // TODO: Call writeback method from the server and try to copy the value from
        // current local copy of the array back to the server
        // Only print "success" or "failure" based on the status of the operation

        throw new UnsupportedOperationException("Writeback not implemented");
    }

    private static void releaseLock() {
        // Replace this with the logic to release a lock on an element

        // TODO: call the releaseLock method on the server with element id

        throw new UnsupportedOperationException("Release_Lock not implemented");
    }

}
