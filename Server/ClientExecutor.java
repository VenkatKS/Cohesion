import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Venkat on 4/9/16.
 */
public class ClientExecutor implements Runnable
{
    Socket activeSocket = null;
    Interpreter myInterpreter = null;

    public ClientExecutor(Socket establishedConnection, Interpreter helper)
    {
        activeSocket = establishedConnection;
        myInterpreter = helper;
    }

    @Override
    public void run()
    {
        if (activeSocket == null) return;
        try
        {
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(activeSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(activeSocket.getOutputStream());

            String myClientCommand = inFromClient.readLine();

            if (Server.DEBUG && Server.VERBOSE) System.out.println("Command recx");
            String serverResponse = myInterpreter.executeCommand(myClientCommand);

            outToClient.write(serverResponse.getBytes());

            outToClient.close();
            inFromClient.close();
            activeSocket.close();

            return;

        } catch (IOException e)
        {
            System.out.println("Error communicating with client.");
        } catch (Interpreter.IllegalCommand illegalCommand)
        {
            if (Server.DEBUG) System.out.println("Client sent an illegal command: " + illegalCommand.getMessage());
        }


    }
}
