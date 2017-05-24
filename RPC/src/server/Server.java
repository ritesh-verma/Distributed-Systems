package server;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import util.FileStore;

import java.net.InetAddress;

public class Server {

    private static FileServiceHandler handler;

    private static FileStore.Processor processor;

    private static int port;

    public static void main(String [] args) {
        if (args.length < 1) {
            System.out.println("Please enter port number.");
            System.exit(1);
        }

        try {
            handler = new FileServiceHandler();
            processor = new FileStore.Processor(handler);
            port= Integer.valueOf(args[0]);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };
      Runnable secure = new Runnable() {
        public void run() {
          secure(processor);
        }
      };
            new Thread(simple).start();
//            new Thread(secure).start();

        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    private static void simple(FileStore.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
//            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
             TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Server address: " + InetAddress.getLocalHost().getHostName());
            System.out.println("Port: " + port);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void secure(FileStore.Processor processor) {
        try {
      /*
       * Use TSSLTransportParameters to setup the required SSL parameters. In this example
       * we are setting the keystore and the keystore password. Other things like algorithms,
       * cipher suites, client auth etc can be set.
       */
            TSSLTransportParameters params = new TSSLTransportParameters();
            // The Keystore contains the private key
            params.setKeyStore("/home/yaoliu/src_code/local/lib/java/test/.keystore", "thrift", null, null);

      /*
       * Use any of the TSSLTransportFactory to get a server transport with the appropriate
       * SSL configuration. You can use the default settings if properties are set in the command line.
       * Ex: -Djavax.net.ssl.keyStore=.keystore and -Djavax.net.ssl.keyStorePassword=thrift
       *
       * Note: You need not explicitly call open(). The underlying server socket is bound on return
       * from the factory class.
       */
            TServerTransport serverTransport = TSSLTransportFactory.getServerSocket(port, 0, null, params);
//            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

            // Use this for a multi threaded server
             TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the secure server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

