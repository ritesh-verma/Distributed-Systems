package branch;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import thrift.Branch;
import util.FileProcessor;

import java.io.IOException;
import java.net.InetAddress;

public class BranchServer {
    private static BranchImpl handler;
    private static Branch.Processor processor;
    private static final String fileName = "branches.txt";

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Please enter two arguments.");
            System.exit(1);
        }

        String branchName = args[0];
        int port;

        FileProcessor fp;

        try {
            port = Integer.parseInt(args[1]);
            fp = new FileProcessor(fileName);
            writeBranchDetails(fp, branchName, port);
            handler = new BranchImpl(branchName, InetAddress.getLocalHost().toString(), port);
            processor = new Branch.Processor(handler);
            Thread thread = new Thread(handler);
            thread.start();

            // Start branch server
            startServer(processor, port);
        } catch (IOException | TTransportException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startServer(Branch.Processor processor, int port) throws TTransportException {
        TServerTransport serverTransport = new TServerSocket(port);
        TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

        server.serve();
    }

    private static void writeBranchDetails(FileProcessor fp, String branchName, int port) throws IOException {
        fp.writeToFile(branchName + " " +
                InetAddress.getLocalHost() + " " +
                port + "\n");
    }
}
