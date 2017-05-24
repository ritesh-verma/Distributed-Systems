package client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import util.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Client {

    public static void main(String [] args) {

        if (args.length < 6) {
            System.out.println("Please enter <ip> <port> --operation <operation> --filename <filename> --user <user>");
            System.exit(0);
        }

        String operation = getArgument(args, "--operation");
        String user = getArgument(args, "--user");
        String fileName = getArgument(args, "--filename");

        try {
            TTransport transport;
            transport = new TSocket(args[0], Integer.valueOf(args[1]));
            transport.open();

            TProtocol protocol = new  TBinaryProtocol(transport);
            FileStore.Client client = new FileStore.Client(protocol);

            perform(client, operation, fileName, user);

            transport.close();
        } catch (TException x) {
            System.out.println("The requested server is not available.");
            x.printStackTrace();
            System.exit(1);
        }
    }

    private static String getArgument(String[] args, String type) {
        return args[Arrays.asList(args).indexOf(type) + 1];
    }

    private static void perform(FileStore.Client client, String operation, String filename, String user) throws TException {

        TIOStreamTransport tioStreamTransport = new TIOStreamTransport(System.out);
        TJSONProtocol tjsonProtocol = new TJSONProtocol(tioStreamTransport);

        RFile rFile = new RFile();
        RFileMetadata rFileMetadata = new RFileMetadata();

        switch (operation) {
            case "read":
                try {
                    rFile = client.readFile(filename, user);
                    System.out.println(rFile.getContent());
                    rFile.write(tjsonProtocol);
                } catch (SystemException se) {
                    se.write(tjsonProtocol);
                }
                break;
            case "write":
                String content = readFromFile(filename);

                rFile.setContent(content);
                rFileMetadata.setFilename(filename.substring(filename.lastIndexOf("/") + 1));
                rFileMetadata.setVersion(0);
                rFileMetadata.setOwner(user);
                rFileMetadata.setContentLength(content.length());

                rFile.setMeta(rFileMetadata);
                try {
                    StatusReport statusReport = client.writeFile(rFile);
                    statusReport.write(tjsonProtocol);
                } catch (SystemException se) {
                    se.write(tjsonProtocol);
                }
                break;
            case "list":
                try {
                    List<RFileMetadata> fileList = client.listOwnedFiles(user);
                    for (int i = 0; i < fileList.size(); i++) {
                        fileList.get(i).write(tjsonProtocol);
                    }
                } catch (SystemException se) {
                    se.write(tjsonProtocol);
                }
                break;
            default:
                System.out.println("Incorrect operation choice.");
                break;
        }
    }

    private static String readFromFile(String filename) {
        StringBuilder content = new StringBuilder();
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error while reading from input file.");
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content.toString();
    }
}

