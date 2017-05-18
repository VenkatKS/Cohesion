import java.io.IOException;
import java.net.Socket;

/**
 * Created by Venkat on 4/6/16.
 */

/*
    NOTE:   The client communicator is used for spawning client executor threads that actually participate in executing what
            the client wants done.
 */

public class ClientCommunicator implements Runnable
{

    public ClientCommunicator()
    {

    }


    @Override
    public void run()
    {
        Socket connectedSocket = null;
        while(true)
        {
            try
            {
                connectedSocket = Networking.acceptNextConnection();

                if (Server.DEBUG && Server.VERBOSE) System.out.println("Accepted Client Connection: " + connectedSocket.getInetAddress().getHostAddress() + " : " + connectedSocket.getLocalPort());
            }
            catch (IOException e)
            {
                System.out.println("One of the servers refused connection.");
                return;
            } catch (InterruptedException e)
            {
                continue;
            }

            if (connectedSocket != null)
            {
                Interpreter TCPInterpreter = new Interpreter();
                Thread newThread = new Thread(new ClientExecutor(connectedSocket, TCPInterpreter));
                newThread.start();
            }
        }





    }
}
