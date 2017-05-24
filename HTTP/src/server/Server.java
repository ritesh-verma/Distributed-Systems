package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;


    private Server() throws IOException {
        serverSocket = new ServerSocket(0);
    }

    private void startServer() {
        File serverDir = new File("www");
        if (!serverDir.exists() || serverDir.isFile()) {
            System.out.println("Resource directory 'www' not present.");
            return;
        }
        SocketAddress localAddress = serverSocket.getLocalSocketAddress();
        System.out.println("Server Address: " + localAddress.toString());

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ExecutorService executor = Executors.newCachedThreadPool();
                Service service = new Service(socket);
                executor.execute(service);
            } catch (IOException e) {
                System.out.println("Error starting server.");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {

        Server server;
        try {
            server = new Server();
            server.startServer();
        } catch (IOException e) {
            System.out.println("IO Exception in creating server.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
