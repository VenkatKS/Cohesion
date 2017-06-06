public class Client
{
    public static void main(String[] args)
    {
        /* Acquire all the information about the servers */
        SessionInfo.generateSessionInformation();

        /* Set-up Connections */
        SessionInfo.setupConnections();

        /* Start The Shell - Should never exit */
        Shell.Interpreter();
    }
}
