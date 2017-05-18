import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Venkat on 4/5/16.
 */
public class Graph
{
    private static ArrayList<Node>              createdNodes            = new ArrayList<Node>();
    private static ArrayList<NodeCommunicator>  activeCommunicators     = new ArrayList<NodeCommunicator>();
    private static HashMap<Integer, Node>       createdNodesMap         = new HashMap<Integer, Node>();


    public synchronized static void newNode(Node newNode)
    {
        createdNodes.add(newNode);
        createdNodesMap.put(newNode.getID(), newNode);
    }

    public synchronized static void removeNode(Node nodeToRemove)
    {
        createdNodes.remove(nodeToRemove);
    }

    public synchronized static Node getNode(String IP, int port)
    {
        for (Node nextNode : createdNodes)
        {
            if (Server.DEBUG) System.out.println("Comparing: " + nextNode.getNodeIP() + " with " + IP + ", and " + nextNode.getNodePort() + " with " + port);
            if (nextNode.getNodeIP().equals(IP) && nextNode.getNodePort() == port) return nextNode;
        }

        return null;
    }

    public synchronized static Node getNodeFromID(int nodeID)
    {
        return createdNodesMap.get(nodeID);
    }

    // REMEMBER: Does not return pointer to actual node store
    public synchronized static ArrayList<Node> getAllNodes()
    {
        ArrayList<Node> returnList = new ArrayList<Node>(createdNodes);
        return returnList;
    }

    public synchronized static int getNumberOfActiveCommunications()
    {
        return activeCommunicators.size();
    }

    public synchronized static void newCommunicator(NodeCommunicator newCommunicator)
    {
        activeCommunicators.add(newCommunicator);
    }

    public synchronized static void deadCommunicator(NodeCommunicator newCommunicator)
    {
        activeCommunicators.remove(newCommunicator);
    }



}
