/**
 * This class serves as the host for text files that
 * users can create, edit, and delete via a client 
 * connecting to this server.
 * 
 * @author Chris Myhre
 * @version 1.0 (CS-310 Final Project)
 * Fall 2024 (2024-12-09)
 */
import java.io.BufferedReader; //bring a shared buffer between multiple instances
import java.io.IOException;//Handle input/output errors
import java.io.InputStreamReader; //used to read text from clients
import java.io.PrintWriter; //Formats the strings sent and received from the clients
import java.net.Socket; //adds an endpoint for the server to send and receive messages from the clients
import java.net.ServerSocket; //Enables the server to listen for client connections
import java.util.concurrent.Semaphore; //Prevents multiple clients from editing the text buffer at the same time

/**
 * Multi-threaded Server that allows clients to perform operations on a shared text buffer.
 * Clients can connect to the server to add, view, or delete text using specific commands.
 * The server uses a semaphore to ensure synchronization and prevent concurrent modification of the shared text buffer.
 */
public class Server {
    private static final StringBuilder textBuffer = new StringBuilder(); // Shared text buffer
    private static final Semaphore semaphore = new Semaphore(1); // Semaphore to control access to the text buffer

    public static void main(String[] args) {
        int port = 12345;
        /*
         * This try block attempts to create a new socket on the local server for a client or clients to connect
         * to so they can edit text files stored (or store txt files) on the server.
         */
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }//end while loop
        }//end try block
        catch (IOException e) {
            e.printStackTrace();
        }//end catch block
    }//end main()

    /*
     * Integrated Class to handle client actions, implemented within another class for simplicity sake. After
     * looking up online the use of nested classes, I learned that for small projects, it's ideal to do this 
     * instead of splitting the Server and ClientHandler classes into their own files.
     */
    static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }//end ClientHandler() constructor

        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
                String clientCommand;
             // Read and process client commands until "EXIT" is received
                while ((clientCommand = in.readLine()) != null) {
                    if (clientCommand.equalsIgnoreCase("EXIT")) {
                        break; // Exit the loop if client sends "EXIT"
                    }//end if statement
                    processCommand(clientCommand, out); // Process the client's command
                }//end while loop
            }//end try block
            catch (IOException e) {
                //e.printStackTrace();
            	System.out.println("A client has disconnected.");
            }//end catch()
        }//end run()
        
        /**
         * Processes the command received from the client.
         * Synchronizes access to the shared text buffer using a semaphore.
         *
         * @param command the command sent by the client
         * @param out     the output stream to send responses to the client
         */
        private void processCommand(String command, PrintWriter out) {
            try {
                semaphore.acquire(); //prevent multiple clients from editing a file at once, reject clients if one is already editing
                // Process "ADD" command: Append text to the shared buffer
                if (command.startsWith("ADD ")) {
                    textBuffer.append(command.substring(4)).append("\n");
                    out.println("Text added successfully.");
                }//end if statement
             // Process "VIEW" command: Display the current contents of the shared buffer
                else if (command.equalsIgnoreCase("VIEW")) {
                    out.println("Current text:\n" + textBuffer.toString());
                }//end if statement
             // Process "DELETE" command: Clear the shared buffer
                else if (command.equalsIgnoreCase("DELETE")) {
                    textBuffer.setLength(0);
                    out.println("Text buffer cleared.");
                }//end if statement
             // Handle unknown commands
                else {
                    out.println("Unknown command.");
                }//end else statement
            }//end try block 
            catch (InterruptedException e) {
                out.println("Operation interrupted.");
            }//end catch block
            finally {
                semaphore.release();//release the text Buffer for other clients to edit.
            }//end finally block
        }//end processCommand()
    }//end ClientHandler class
}//end Server class