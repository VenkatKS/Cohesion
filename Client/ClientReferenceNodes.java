/**
 * Created by Venkat on 4/9/16.
 */
public class ClientReferenceNodes
{
    /* Class represents a server in the graph */
    private int     nodeID;
    private String  nodeIP;
    private int     nodePort;

    public ClientReferenceNodes(int nodeID, String nodeIP, int nodePort)
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
        ClientReferenceNodes compareNode = (ClientReferenceNodes) compareTo;

        if (((ClientReferenceNodes) compareTo).getNodeIP().equals(nodeIP) && ((ClientReferenceNodes) compareTo).getNodePort()==nodePort) return true;

        return false;
    }

    @Override
    public int hashCode()
    {
        return nodePort;
    }
}
