
/**
 * Created by Venkat on 6/3/17.
 */
public class SessionInfo
{
    /* The total number of Servers that exist */
    private static int numberOfServers;

    /* The current server to communicate to */
    private static int referenceName = 0;
    static ClientReferenceNodes currentNode;


    private static void askForServersNumbers()
    {
        System.out.println("Please enter the number of servers that exist:");
        numberOfServers = StandardIOCommunicator.getNextInteger();
    }

    private static void getPeerServers()
    {
        StandardIOCommunicator.nextLine();

        for (int i = 0; i < numberOfServers; i++)
        {
            System.out.println("Please enter Server " + i + "'s IP: ");
            String input = StandardIOCommunicator.nextLine();
            String[] ipPortSplit = input.split(":");

            /* Get the new server's properties */
            int newServerID = i;
            String newServerIP = ipPortSplit[0];
            int newServerPort = Integer.parseInt(ipPortSplit[1]);

            /* A new server is being added */
            ClientReferenceNodes newNode = new ClientReferenceNodes(newServerID, newServerIP, newServerPort);

            ClientReferenceGraph.newNode(newNode);
        }
        currentNode = ClientReferenceGraph.getAllNodes().get(0);
    }

    public static void generateSessionInformation()
    {
        askForServersNumbers();
        getPeerServers();
    }

    public static void setupConnections()
    {
        int referenceName = 0;
        ClientReferenceNodes currentNode = ClientReferenceGraph.getAllNodes().get(referenceName);
    }

    public static ClientReferenceNodes getCurrentNode()
    {
        return currentNode;
    }

    public static int getReferenceName()
    {
        return referenceName;
    }
}

