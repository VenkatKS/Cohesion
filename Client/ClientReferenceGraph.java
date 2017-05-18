import java.util.ArrayList;

/**
 * Created by Venkat on 4/9/16.
 */
public class ClientReferenceGraph
{
    private static ArrayList<ClientReferenceNodes> clientRefNodes = new ArrayList<ClientReferenceNodes>();

    public synchronized static void newNode(ClientReferenceNodes newNode)
    {
        clientRefNodes.add(newNode);
    }

    public synchronized static void removeNode(ClientReferenceNodes nodeToRemove)
    {
        clientRefNodes.remove(nodeToRemove);
    }

    public synchronized static ClientReferenceNodes getNode(String IP, int port)
    {
        for (ClientReferenceNodes nextNode : clientRefNodes)
        {
            if (nextNode.getNodeIP().equals(IP) && nextNode.getNodePort() == port) return nextNode;
        }

        return null;
    }

    // REMEMBER: Does not return pointer to actual node store
    public synchronized static ArrayList<ClientReferenceNodes> getAllNodes()
    {
        ArrayList<ClientReferenceNodes> returnList = new ArrayList<ClientReferenceNodes>(clientRefNodes);
        return returnList;
    }

}
