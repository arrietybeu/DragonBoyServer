package nro.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Arriety
 */
public class MainServer implements Runnable {

//    public static void main(String[] args) throws IOException {
////        for (int i = 0; i < 1000; i++) {
////            UUID uuid = UUID.randomUUID();
////            int id = (int) (uuid.getMostSignificantBits() & Short.MAX_VALUE);
////            System.out.println("Generated int ID: " + id);
////        }
//
//        for (int i = 0; i < 1000; i++) {
//            Thread thread = new Thread(new MainServer());
//            thread.start();
//        }
//    }

    @Override
    public void run() {
        String serverAddress = "127.0.0.1";
        int serverPort = 14445; // Cổng server

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            System.out.println("Connected to server: " + serverAddress + ":" + serverPort);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            String message;
            System.out.println("Type 'exit' to quit.");
            while (true) {
                System.out.print("Enter message to send: ");
                message = scanner.nextLine();

                if ("exit".equalsIgnoreCase(message)) {
                    System.out.println("Closing connection...");
                    break;
                }

                out.println(message);

                String response = in.readLine();
                System.out.println("Server response: " + response);
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        } finally {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
