package server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class Service implements Runnable {

    private DataOutputStream out;
    private StringBuffer clientRequest;
    private Socket newServer;
    private static volatile Map<String, Integer> fileCount = new HashMap<>();

    Service(Socket newServerIn) {
        newServer = newServerIn;
    }

    private String createConnection(Socket server) {

        try {
            DataInputStream in = new DataInputStream(server.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(in)));
            out = new DataOutputStream(server.getOutputStream());

            clientRequest = new StringBuffer();
            clientRequest.append(br.readLine());
        } catch (IOException e) {
            System.out.println("Error in creating connection." + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        return clientRequest.toString();
    }

    private void writeResponse(String response, File file) throws IOException {

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(out)));

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
            bufferedOutputStream.write(response.getBytes(), 0, response.length());

            try {
                byte[] buffer = new byte[4096];
                int readLength = 0;
                while ((readLength = bufferedInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, readLength);
                }
            } catch (IOException e) {
                System.out.println("IO Exception while writing response." + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            } finally {
                bufferedInputStream.close();
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                out.close();
            }
    }

    private void writeError(String response) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream("error_404.html"));
        BufferedOutputStream bout = new BufferedOutputStream(out);
        try {
            bout.write(response.getBytes(), 0, response.length());
            byte[] buffer = new byte[4096];
            int readLen = 0;
            while ((readLen = bin.read(buffer)) != -1) {
                bout.write(buffer, 0, readLen);
            }
        } catch (IOException e) {
            System.out.println("Error while writing response.");
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                bout.flush();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getFile(Response response) {
        return new File("www/" + response.getRequestFile());
    }

    @Override
    public void run() {
        Response response = new Response(createConnection((newServer)));
        String responseString = "";
            File file = getFile(response);
        try {
            responseString = response.generateResponse(file);
            if (file.exists()) {
                String fileName = file.getName();
                Integer count = fileCount.get(fileName);
                if (count == null) {
                    count = new Integer(1);
                    fileCount.put(fileName, count);
                } else {
                    count++;
                    fileCount.put(fileName, count);
                }
                writeResponse(responseString, file);
                System.out.println("/" + file.getName() + "|" + newServer.getRemoteSocketAddress() + "|" + count);
            } else {
                writeError(responseString);
            }
        } catch (IOException e) {
            System.out.println("IO Exception in thread.");
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                newServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
