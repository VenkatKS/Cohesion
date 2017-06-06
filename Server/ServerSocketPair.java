import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Semaphore;

/**
 * Created by Venkat on 4/5/16.
 */
public class ServerSocketPair
{
    private ServerSocket TCPServerSocket;
    private Semaphore SocketMutex;


    public ServerSocketPair(int portNum) throws IOException
    {
        TCPServerSocket = new ServerSocket(portNum);
        SocketMutex     = new Semaphore(1);
    }

    // Blocking call for socket
    /*
        @param: none
        @postcondition: MUST call releaseSocket after socket is done
     */
    public ServerSocket acquireSocket() throws InterruptedException
    {
        SocketMutex.acquire();
        return TCPServerSocket;
    }

    public void releaseSocket()
    {
        SocketMutex.release();
    }
}
