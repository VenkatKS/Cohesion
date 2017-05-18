/**
 * Created by Venkat on 4/5/16.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public class Server
{
    public static final boolean DEBUG = false;
    public static final boolean VERBOSE = false;


    public static void main (String[] args) {

        if (!DEBUG)
        {
            System.out.println("What's my server ID?");
            Scanner sc = new Scanner(System.in);
            int myID = 0;
            try
            {
                myID = sc.nextInt();
            } catch (Exception e)
            {
                System.out.println("Please only enter a number!");
                System.exit(-1);
            }
            System.out.println("How many servers are there (including me)?");
            int numServer = 0;
            try
            {
                numServer = sc.nextInt();
            } catch (Exception e) {
                System.out.println("Please only enter a number!");
                System.exit(-1);
            }

            System.out.println("What's the path to the inventory file that I need to manage?");
            String inventoryPath = sc.next();

            // Update information
            CurrentNodeProperties.filePath     = inventoryPath;
            CurrentNodeProperties.nodeID       = myID;
            CurrentNodeProperties.numNodes     = numServer;

            System.out.println("Please enter the addresses of all servers (in order of server IDs -- i.e." +
                    " Server 0's ID first, then Server 1's, etc.)!");
            sc.nextLine();
            for (int i = 1; i <= numServer; i++) {
                // TODO: parse inputs to get the ips and ports of servers

                /* Read the server properties from STDIN */
                String input = sc.nextLine();
                String[] ipPortSplit = input.split(":");

                /* Get the new server's properties */
                int newServerID = i;
                String newServerIP = ipPortSplit[0];
                int newServerPort = Integer.parseInt(ipPortSplit[1]);

                /* A new server is being added */
                Node newNode = new Node(newServerID, newServerIP, newServerPort);

                /* NOTE: The current server is also added to the graph */

                /* NOTE: Set current server's identity */
                if (i == CurrentNodeProperties.nodeID)
                {
                    System.out.println("I'm assuming what you just entered is my IP info (due to my server ID)!");
                    CurrentNodeProperties.nodeIP = newServerIP;
                    CurrentNodeProperties.nodePort = newServerPort;
                }
                else
                {
                    System.out.println("External Server Registered!");
                    Graph.newNode(newNode);
                }
            }
        }
        else
        {
            CurrentNodeProperties.nodeID = Integer.parseInt(args[0]);    // The ID of the current server running

            /* We're debugging, so use a statically defined server set */
            if (CurrentNodeProperties.nodeID == 2)
            {
                CurrentNodeProperties.nodeIP = "127.0.0.1";
                CurrentNodeProperties.nodePort = 9005;

                CurrentNodeProperties.filePath     = "inventory.txt";
                CurrentNodeProperties.numNodes     = 3;

                Node NodeOne    = new Node(1, "127.0.0.1", 9000);
                Node NodeTwo    = new Node(2, "127.0.0.1", 9005);
                Node NodeThree  = new Node(3, "127.0.0.1", 9010);

                Graph.newNode(NodeOne);
                //Graph.newNode(NodeTwo);
                Graph.newNode(NodeThree);
            }
            else if (CurrentNodeProperties.nodeID == 1)
            {
                CurrentNodeProperties.nodeIP = "127.0.0.1";
                CurrentNodeProperties.nodePort = 9000;

                CurrentNodeProperties.filePath     = "inventory.txt";
                CurrentNodeProperties.numNodes     = 3;

                Node NodeOne    = new Node(1, "127.0.0.1", 9000);
                Node NodeTwo    = new Node(2, "127.0.0.1", 9005);
                Node NodeThree  = new Node(3, "127.0.0.1", 9010);

                //Graph.newNode(NodeOne);
                Graph.newNode(NodeTwo);
                Graph.newNode(NodeThree);

            }
            else
            {
                CurrentNodeProperties.nodeIP = "127.0.0.1";
                CurrentNodeProperties.nodePort = 9010;

                CurrentNodeProperties.filePath     = "inventory.txt";
                CurrentNodeProperties.numNodes     = 3;

                Node NodeOne    = new Node(1, "127.0.0.1", 9000);
                Node NodeTwo    = new Node(2, "127.0.0.1", 9005);
                Node NodeThree  = new Node(3, "127.0.0.1", 9010);

                Graph.newNode(NodeOne);
                Graph.newNode(NodeTwo);
                //Graph.newNode(NodeThree);
            }

        }

        System.out.println("Great! I'll set up connections now by sending Lamport Messages to the other servers to" +
                " establish coherency between these servers.");
        // TODO: start server socket to communicate with clients and other servers

        /* NOTE: Bind this server to the server's port and IP location */
        Networking.BindServerSocket(CurrentNodeProperties.nodePort);

        /* NOTE: Start listening to all the other server's requests for connections. */
        Networking.startListenersForSelectedNodes(Graph.getAllNodes());

        /* NOTE: Start sending out connection requests to everyone */
        Networking.establishConnectionsWithSelectedNodes(Graph.getAllNodes());

        /* NOTE: Wait until all connections are stable before continuing */
        Networking.waitUntilSelectedConnectionsAreStable(Graph.getAllNodes().size());

        /*
            NOTE: Working under the assumption that clients do not connect until all servers connect
        */
        System.out.println("Awesome! I've talked to the other servers and we're on the same page." +
                " I'll update my inventory now.");
        /* NOTE: We parse the inventory file here and update the array with the items that the inventory file features */
        LocalInventoryFunctions.StartUpdatingInventory(CurrentNodeProperties.filePath);

        System.out.println("I'm all set! I can accept client connections now.");
        // TODO: handle request from client
        try
        {
            Thread TCPMaster = new Thread(new ClientCommunicator());
            TCPMaster.start();
            TCPMaster.join();
        }
        catch(InterruptedException g)
        {
            // TODO: Handle this case
        }



    }
}
