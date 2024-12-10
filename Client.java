/**
 * This class serves as an interface for the user to communicate various commands to the server
 * to change, view or delete the text buffer.
 * 
 * @author Chris Myhre
 * @version 1.0 (CS-310 Final Project)
 * Fall 2024 (2024-12-09)
 */
import java.io.BufferedReader; //bring a shared buffer between multiple instances
import java.io.IOException; //Handle input/output errors
import java.io.InputStreamReader; //used to read text from the server
import java.io.PrintWriter; // Formats the strings sent and received from the server
import java.net.Socket; //adds an endpoint for the client to send and receive messages from the server

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        /**
         * Establishes a connection to the server using a socket.
         * Streams are used for sending commands to the server and receiving responses.
         * User input is read from the command line, and commands are sent to the server
         * until the user decides to exit the program.
         */
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server.");
            System.out.println("Commands: ADD <text>, VIEW, DELETE, EXIT");

            String input;
         // Loop to continuously read user input and send commands to the server
            while ((input = userInput.readLine()) != null) {
                out.println(input); // Send command to server
                if (input.equalsIgnoreCase("EXIT")) {
                    break; // Exit loop if the user enters "EXIT"
                }//end if statement
                // Print the server's response to the command
                System.out.println("Server response: " + in.readLine());
            }//end while loop
        }//end try block
        catch (IOException e) {
        	System.out.println("Error connecting to the server: " + e.getMessage());
            e.printStackTrace();
        }//end catch block
    }//end main()
}//end Client class