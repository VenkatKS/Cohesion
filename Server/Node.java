import java.util.Comparator;

/**
 * Created by Venkat on 4/5/16.
 */
public class Node
{
    /* Class represents a server in the graph */
    private int     nodeID;
    private String  nodeIP;
    private int     nodePort;

    public Node(int nodeID, String nodeIP, int nodePort)
    {
        this.nodeID     = nodeID;
        this.nodeIP     = nodeIP;
        this.nodePort   = nodePort;
    }


    // The fields cannot be set once created
    public int getID()
    {
        return nodeID;
    }

    public String getNodeIP()
    {
        return nodeIP;
    }

    public int getNodePort()
    {
        return nodePort;
    }

    @Override
    public boolean equals(Object compareTo)
    {
        Node compareNode = (Node) compareTo;

        if (((Node) compareTo).getNodeIP().equals(nodeIP) && ((Node) compareTo).getNodePort()==nodePort) return true;

        return false;
    }

    @Override
    public int hashCode()
    {
        return nodePort;
    }

    public String toString() { return new String("::: ID: " + nodeID + " (IP: " + nodeIP + ", Port: " + nodePort +") :::") ;}
}
