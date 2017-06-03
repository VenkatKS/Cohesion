import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

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
