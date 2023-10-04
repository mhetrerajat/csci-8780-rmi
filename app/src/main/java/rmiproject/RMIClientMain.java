package rmiproject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

public class RMIClientMain {

    private static final Logger logger = Logger.getLogger(RMIClientMain.class.getName());

    // client id assigned by the server
    private static Integer clientId;

    // Local copy of the remote array
    private static ArrayList<String> localArray;

    // RemoteStringArray
    static RemoteStringArray stub;

    public RMIClientMain() throws RemoteException, NotBoundException {
        // Get configuration values using ConfigReader
        String serverHost = ConfigReader.getServerHost();
        Integer serverPort = ConfigReader.getServerPort();
        String bindName = ConfigReader.getRemoteObjectBindName();

        // Get the registry
        Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);

        // Lookup the remote object from the registry
        stub = (RemoteStringArray) registry.lookup(bindName);

        // init local copy of the array same size as of remote array
        localArray = new ArrayList<>(stub.getRemoteArrayCapacity());
        // get the client id from the server
        clientId = stub.getClientId();

    }

    public static void main(String[] args) throws RemoteException {

        // TODO: Automatically release the locks if the client gets killed / crashed
        // TODO: Remove the throws statement and add try-catch block to print that
        // server has crashed message

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
                    getArrayCapacity();
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

    private static void getArrayCapacity() throws RemoteException {
        logger.info(String.format("Server Response - Array Capacity : %d", stub.getRemoteArrayCapacity()));
    }

    private static void fetchElementRead() throws RemoteException {
        // TODO: Modify the CLI to ask for the index from the user

        Integer index = 0;
        Optional<String> arrElement = Optional.ofNullable(stub.fetchElementRead(index, clientId));

        arrElement.ifPresentOrElse(
                element -> {
                    localArray.add(index, element);
                    logger.info(String.format("[Success]: Client[%d] updated localArray[%d] = %s", clientId, index,
                            element));
                },
                () -> {
                    logger.warning(String.format(
                            "[Failure]: Client[%d] does not have the permission to fetch %d(st/rd/th) element from remote array",
                            clientId, index));
                });

    }

    private static void fetchElementWrite() throws RemoteException {
        // TODO: Modify the CLI to ask for the index from the user

        Integer index = 0;
        Optional<String> arrElement = Optional.ofNullable(stub.fetchElementWrite(index, clientId));

        arrElement.ifPresentOrElse(
                element -> {
                    localArray.add(index, element);
                    logger.info(String.format("[Success]: Client[%d] updated localArray[%d] = %s", clientId, index,
                            element));
                },
                () -> {
                    logger.warning(String.format(
                            "[Failure]: Client[%d] does not have the permission to fetch %d(st/rd/th) element with write permission from remote array",
                            clientId, index));
                });
    }

    private static void printElement() {
        // TODO: Modify the CLI to ask for the index from the user

        Integer index = 0;
        logger.info(String.format("[Success]: Client[%d] printed local copy of %d(st/rd/th) element - %s",
                clientId, index, localArray.get(index)));
    }

    private static void concatenate() {
        // TODO: Modify the CLI to ask for the index and string to concat from the user
        Integer index = 0;
        String stringToConcat = "abc";

        String newString = localArray.get(index).concat(stringToConcat);
        localArray.add(index, newString);
        logger.info(String.format("[Success]: Client[%d] %d(st/rd/th) string after concatenation operation: %s",
                clientId, index, newString));
    }

    private static void writeback() throws RemoteException {

        // TODO: Modify the CLI to ask for the index from the user
        Integer index = 0;
        
        String localValue = localArray.get(index);
        boolean isSuccessful = stub.WriteBackElement(localValue, index, clientId);
        if (isSuccessful) {
            logger.info(String.format("[Success]: Client[%d] write back localArray[%d] value %s to server.", clientId,
                    index,
                    localValue));
        } else {
            logger.warning(String.format(
                    "[Failure]: Client[%d] does not have the permission to write %d(st/rd/th) element to remote array",
                    clientId, index));
        }
    }

    private static void releaseLock() {
        // Replace this with the logic to release a lock on an element

        // TODO: call the releaseLock method on the server with element id

        throw new UnsupportedOperationException("Release_Lock not implemented");
    }

}
