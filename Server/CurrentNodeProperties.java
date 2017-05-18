/**
 * Created by Venkat on 4/5/16.
 */
public class CurrentNodeProperties
{
    // Current node's ID number
    static int nodeID;

    // Current node's IP
    static String nodeIP;

    // Current node's Port number
    static int nodePort;

    // Inventory file path
    static String filePath;

    // Global properties:

    // Total number of nodes
    static int numNodes;
    static int numNodeAlive;

    // Mutual Exclusion Variables
    static CausalClock currentClock = new CausalClock(0);

}
