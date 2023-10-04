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
            Integer index;

            switch (choice) {
                case 1:
                    getArrayCapacity();
                    break;
                case 2:
                    index = getIndexViaCLI(scanner);
                    fetchElementRead(index);
                    break;
                case 3:
                    index = getIndexViaCLI(scanner);
                    fetchElementWrite(index);
                    break;
                case 4:
                    index = getIndexViaCLI(scanner);
                    printElement(index);
                    break;
                case 5:
                    index = getIndexViaCLI(scanner);
                    String concatStr = getStringToConcatenateViaCLI(scanner);
                    concatenate(index, concatStr);
                    break;
                case 6:
                    index = getIndexViaCLI(scanner);
                    writeback(index);
                    break;
                case 7:
                    index = getIndexViaCLI(scanner);
                    releaseLock(index);
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

    public static int getIndexViaCLI(Scanner scanner) {
        int index = 0;
        boolean validInput = false;
        Integer maxArrSize = localArray.size();

        while (!validInput) {
            System.out.print("Enter an index (or type 'exit' to quit): ");
            String userInput = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userInput)) {
                System.exit(0); // Exit the program
            }

            try {
                index = Integer.parseInt(userInput);
                if (index >= 0 && index < maxArrSize) {
                    validInput = true;
                } else {
                    System.out.println("Invalid index. Please enter an index between 0 and " + (maxArrSize - 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer or 'exit' to quit.");
            }
        }

        return index;
    }

    public static String getStringToConcatenateViaCLI(Scanner scanner) {
        String input = "";
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter a string to concatenate (or type 'exit' to quit): ");
            input = scanner.nextLine().trim(); // Trim leading and trailing whitespace

            if ("exit".equalsIgnoreCase(input)) {
                System.exit(0); // Exit the program
            }

            if (!input.isEmpty()) {
                validInput = true; // Valid input, exit the loop
            } else {
                System.out.println("Invalid input. Please enter a non-empty string.");
            }
        }

        return input;
    }

    private static void getArrayCapacity() throws RemoteException {
        logger.info(String.format("Server Response - Array Capacity : %d", stub.getRemoteArrayCapacity()));
    }

    private static void fetchElementRead(Integer index) throws RemoteException {
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

    private static void fetchElementWrite(Integer index) throws RemoteException {
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

    private static void printElement(Integer index) {
        logger.info(String.format("[Success]: Client[%d] printed local copy of %d(st/rd/th) element - %s",
                clientId, index, localArray.get(index)));
    }

    private static void concatenate(Integer index, String stringToConcat) {
        String newString = localArray.get(index).concat(stringToConcat);
        localArray.add(index, newString);
        logger.info(String.format("[Success]: Client[%d] %d(st/rd/th) string after concatenation operation: %s",
                clientId, index, newString));
    }

    private static void writeback(Integer index) throws RemoteException {
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

    private static void releaseLock(Integer index) throws RemoteException {
        stub.releaseLock(index, clientId);
        logger.info(String.format("[Success]: Client[%d] %d(st/rd/th) releases all locks",
                clientId, index));
    }

}
