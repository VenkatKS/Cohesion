import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Venkat on 4/5/16.
 */
public class Networking
{
    static ServerSocketPair BoundedSocket;

    static HashMap<Integer, ObjectStreamPair> outStreams = new HashMap<Integer, ObjectStreamPair>();

    /* Condition variables for connection ready check */
    public static ReentrantLock connectionsLock = new ReentrantLock();
    public static Condition allConnectionsDone = connectionsLock.newCondition();

    public static void BindServerSocket(int portNum)
    {
        try
        {
            BoundedSocket = new ServerSocketPair(portNum);
        }
        catch(IOException e)
        {
            System.out.println("Cannot bind to specified server socket.");
            System.exit(-1);
        }
    }


    public static Socket acceptNextConnection() throws InterruptedException, IOException
    {
        try
        {
            ServerSocket activeSocket = BoundedSocket.acquireSocket();
            Socket newConnection = activeSocket.accept();
            BoundedSocket.releaseSocket();
            return newConnection;
        }
        finally
        {
            BoundedSocket.releaseSocket();
        }
    }

    public static void startListenersForSelectedNodes(ArrayList<Node> existingGraph)
    {
        for (Node nextNode : existingGraph)
        {
            Thread nextCommunicator = new Thread(new NodeCommunicator(BoundedSocket));
            nextCommunicator.start();
        }
    }

    public static void establishConnectionsWithSelectedNodes(ArrayList<Node> existingGraph)
    {
        for(int i = 0; i < existingGraph.size(); i++)
        {
            Node nextNode = existingGraph.get(i);
            try
            {
                Socket ClientSocket = new Socket(nextNode.getNodeIP(), nextNode.getNodePort());
                ObjectOutputStream nodeOutput = new ObjectOutputStream(ClientSocket.getOutputStream());
                newOutputStream(nextNode, nodeOutput);
            }
            catch(IOException e)
            {
                //System.out.println("Error connecting with one of the nodes.");
                // TODO: What to do here?
                i--;
            }
        }
    }

    public static synchronized void newOutputStream(Node connectedNode, ObjectOutputStream nodeStream)
    {
        //if (Server.DEBUG) System.out.println("Adding stream for " + connectedNode.getID() + " :: " + nodeStream);
        if (nodeStream != null) outStreams.put(connectedNode.getID(), new ObjectStreamPair(nodeStream));
    }

    public static void waitUntilSelectedConnectionsAreStable(int numConnectionsNeeded)
    {
        while(true)
        {
            connectionsLock.lock();
            try
            {
                while(Graph.getNumberOfActiveCommunications() < (numConnectionsNeeded))
                    allConnectionsDone.await();

                break;
            }
            catch (InterruptedException e)
            {
                continue; /* Despite being interrupted, continue */
            }
            finally
            {
                connectionsLock.unlock();
            }
        }

    }

    public static void sendToNode(Message msgToSend, int nodeToSendTo)
    {
        ObjectOutputStream runningStream;
        while(true)
        {
            try
            {
                ObjectStreamPair currentPair = outStreams.get(nodeToSendTo);

                /* NOTE: if a stream does not exist, we can't send anything. */
                if (currentPair == null)
                {
                    return;
                }

                runningStream = currentPair.acquireStream();
                break;
            }
            catch (InterruptedException e)
            {
                continue; /* NOTE: We did not get the mutex and while blocking, got interrupted. Keep trying. No need to release. */
            }
        }

        try
        {
            runningStream.writeObject(msgToSend);
        }
        catch(IOException e)
        {
            if (Server.DEBUG) System.out.println("Could not send message.");
            if (Server.DEBUG) System.out.println("Error: " + e + ", message: " + e.getMessage());

        }
        finally
        {
            outStreams.get(nodeToSendTo).releaseStream();
        }

    }

    public static void broadcastMessage(Message msgToSend)
    {
        ArrayList<Node> allNodes = Graph.getAllNodes();

        for (Node nextNode : allNodes)
        {
            sendToNode(msgToSend, nextNode.getID());
        }
    }


}
