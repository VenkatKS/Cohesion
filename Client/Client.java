import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

public class Client
{
    static byte[] rxArray = new byte[10240];
    static byte[] txArray = new byte[10240];

    public static final boolean DEBUG = false;

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter the number of servers that exist:");
        if (!DEBUG)
        {
            int numServer = sc.nextInt();

            sc.nextLine();

            for (int i = 0; i < numServer; i++)
            {
                System.out.println("Please enter Server " + i + "'s IP: ");
                String input = sc.nextLine();
                String[] ipPortSplit = input.split(":");

                    /* Get the new server's properties */
                int newServerID = i;
                String newServerIP = ipPortSplit[0];
                int newServerPort = Integer.parseInt(ipPortSplit[1]);

                /* A new server is being added */
                ClientReferenceNodes newNode = new ClientReferenceNodes(newServerID, newServerIP, newServerPort);

                ClientReferenceGraph.newNode(newNode);
            }
        }
        else
        {
            ClientReferenceGraph.newNode(new ClientReferenceNodes(0, "127.0.0.1", 9000));
            ClientReferenceGraph.newNode(new ClientReferenceNodes(0, "127.0.0.1", 9005));
            ClientReferenceGraph.newNode(new ClientReferenceNodes(0, "127.0.0.1", 9010));

        }

        /* NOTE: Establish connections to every server */
        int referenceName = 0;
        ClientReferenceNodes currentNode = ClientReferenceGraph.getAllNodes().get(referenceName);

        System.out.print("\n>> ");
        boolean deadServer = false;
        String  previousContent = null;
        while (deadServer || sc.hasNextLine())
        {
            for (int i = 0; i < rxArray.length; i++) rxArray[i] = 0;

            String cmd = (deadServer) ? new String(previousContent) : sc.nextLine();
            deadServer = false;

            String[] tokens = cmd.split(" ");

            if (tokens[0].equals("purchase"))
            {
                // TODO: send appropriate command to the server and display the
                String userName = null;
                String prodName = null;
                String quantity = null;
                try {
                    userName = tokens[1];
                    prodName = tokens[2];
                    quantity = tokens[3];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("There was not enough parameters provided.");
                    System.out.print("\n>> ");
                    continue;
                }
                // appropriate responses form the server
            }
            else if (tokens[0].equals("cancel"))
            {
                String orderId = null;
                try
                {
                    orderId   = tokens[1];
                } catch (ArrayIndexOutOfBoundsException e)
                {
                    System.out.println("There was not enough parameters provided.");
                    System.out.print("\n>> ");
                    continue;
                }
            } else if (tokens[0].equals("search"))
            {
                String userName = null;
                try
                {
                    userName   = tokens[1];
                } catch (ArrayIndexOutOfBoundsException e)
                {
                    System.out.println("There was not enough parameters provided.");
                    System.out.print("\n>> ");
                    continue;
                }
            }
            else if (tokens[0].equals("list"))
            {
            }
            else
            {
                System.out.println("ERROR: No such command");
                System.out.print("\n>> ");
                continue;
            }

            try
            {
                Socket clientSocket = new Socket(currentNode.getNodeIP(), currentNode.getNodePort());
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.writeBytes(cmd + "\n");

                char[] inArray = new char[10240];
                int size = inFromServer.read(inArray);
                String serverResponse = new String(inArray, 0 , size);
                clientSocket.close();

                //referenceName = (referenceName + 1)%ClientReferenceGraph.getAllNodes().size();
                //currentNode = ClientReferenceGraph.getAllNodes().get(referenceName);


                System.out.println(serverResponse.toString());
                System.out.print("\n>> ");

            } catch (Exception e)
            {
                ClientReferenceGraph.removeNode(currentNode);

                if (ClientReferenceGraph.getAllNodes().size() == 0)
                {
                    System.out.println("All servers are dead.");
                    System.exit(0);
                }

                referenceName = (referenceName + 1)%ClientReferenceGraph.getAllNodes().size();
                currentNode = ClientReferenceGraph.getAllNodes().get(referenceName);
                deadServer =  true;
                previousContent = new String(cmd);
                if (deadServer) continue;
                if (DEBUG) System.out.println("Cannot communicate with server: " + e.getMessage());
                System.out.print("\n>> ");
            }


        }
    }
}
