import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Venkat on 6/3/17.
 */
public class ServerCommunicator
{
    static String previousContent;
    static Boolean deadServer = false;
    public static void sendCommandToServers(String inputCommand)
    {
        ClientReferenceNodes currentNode = SessionInfo.getCurrentNode();
        int referenceName = SessionInfo.getReferenceName();

        while (true)
        {
            String cmd = (deadServer) ? previousContent : inputCommand;
            deadServer = false;

            try
            {
                Socket clientSocket = new Socket(currentNode.getNodeIP(), currentNode.getNodePort());
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.writeBytes(cmd + "\n");

                char[] inArray = new char[10240];
                int size = inFromServer.read(inArray);
                String serverResponse = new String(inArray, 0, size);
                clientSocket.close();

                System.out.println(serverResponse.toString());
                System.out.print("\n>> ");
                break;
            } catch (Exception e)
            {
                ClientReferenceGraph.removeNode(currentNode);

                if (ClientReferenceGraph.getAllNodes().size() == 0)
                {
                    System.out.println("All servers are dead.");
                    System.exit(0);
                }

                referenceName = (referenceName + 1) % ClientReferenceGraph.getAllNodes().size();
                currentNode = ClientReferenceGraph.getAllNodes().get(referenceName);
                previousContent = new String(cmd);
                deadServer = true;
            }
        }
    }

}
