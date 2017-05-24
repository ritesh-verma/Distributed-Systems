package client;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please enter server name and port number");
            System.exit(1);
        }

        String server = args[0];
        int portNumber;
        Socket clientSocket;

        try {
            portNumber = Integer.parseInt(args[1]);
            clientSocket = new Socket(server, portNumber);
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            outputStream.writeUTF("GET /test.html HTTP/1.1\n" +
                    "User-Agent: Wget/1.17.1 (linux-gnu)\n" +
                    "Accept: */*\n" +
                    "Accept-Encoding: identity\n" +
                    "Host: 0.0.0.0:8080\n" +
                    "Connection: Keep-Alive");

            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            System.out.println(inputStream.readUTF());

            BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
            br.readLine();
//            clientSocket.close();
        } catch (NumberFormatException | IOException e) {
            System.out.println("Input port number invalid." + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
