import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by Venkat on 4/5/16.
 */
public class NodeCommunicator implements Runnable
{
    Node associatedNode = null;
    ServerSocketPair activeBoundSocket;

    public NodeCommunicator(ServerSocketPair activeBoundSocket)
    {
        this.activeBoundSocket  = activeBoundSocket;
    }
    @Override
    public void run()
    {
        /* NOTE: Accepting the connection from a requesting peer */
        Socket connectedSocket;
        while(true)
        {
            try
            {
                connectedSocket = Networking.acceptNextConnection();

                if (Server.DEBUG) System.out.println("Accepted connection: " + connectedSocket.getInetAddress().getHostAddress() + " : " + connectedSocket.getLocalPort());
                break;

            } catch (InterruptedException e)
            {
                /* Could not acquire Semaphore */
                continue; // try again

            }
            catch (IOException e)
            {
                System.out.println("One of the servers refused connection.");
                return;
            }
        }

        ObjectInputStream inStream = null;
        try
        {
            inStream = new ObjectInputStream(connectedSocket.getInputStream());
        }
        catch (IOException e)
        {
            if (Server.DEBUG) System.out.println("Could not open stream to socket.");
            return;
        }

        Graph.newCommunicator(this);

        /* NOTE: Inform the server that a new connection has been established. */
        Networking.connectionsLock.lock();
        Networking.allConnectionsDone.signal();
        Networking.connectionsLock.unlock();

        while(true)
        {
            try
            {
                Message myMsg = (Message) inStream.readObject();
                MutualExclusionEnforcer.OnReceive(myMsg);
            }
            catch (IOException e)
            {
                if (Server.DEBUG) System.out.println("Socket connection cut. " + e);
                System.out.println("Oops! Looks like one of the other servers went offline. I'll update my list.");
                Graph.deadCommunicator(this);
                return;
            }
            catch (ClassNotFoundException e)
            {
                if (Server.DEBUG) System.out.println("Illegal Message");
                continue;
            }
        }




    }

    public void stop()
    {
        Thread.currentThread().interrupt();
    }
}

