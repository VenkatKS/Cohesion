import sun.security.x509.AVA;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Venkat on 6/3/17.
 */
public class Shell
{
    private static ShellCommand[] commands = {

            /* PURCHASE Command */
            new ShellCommand()
            {
                @Override
                public String CommandName()
                {
                    return "purchase";
                }

                @Override
                public void Execute(String cmd)
                {
                    String userName = null;
                    String prodName = null;
                    String quantity = null;

                    String[] tokens = cmd.split(" ");

                    /* Verify if the provided command is formatted properly */
                    try {

                        userName = tokens[1];
                        prodName = tokens[2];
                        quantity = tokens[3];

                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("There was not enough parameters provided.");
                        System.out.print("\n>> ");
                        return;
                    }

                    ServerCommunicator.sendCommandToServers(cmd);
                }
                @Override
                public String helpText()
                {
                    StringBuilder helpString = new StringBuilder("PURCHASE <username> <product name> <quantity>: ");
                    helpString.append("If enough is present, purchases <quantity> amount of" +
                            "<product name> under the customer name <username>");
                    return helpString.toString();
                }

            },

            /* SEARCH Command */
            new ShellCommand()
            {
                @Override
                public String CommandName()
                {
                    return "search";
                }

                @Override
                public void Execute(String cmd)
                {
                    String userName = null;

                    String[] tokens = cmd.split(" ");

                    try {
                        userName   = tokens[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("There was not enough parameters provided.");
                        System.out.print("\n>> ");
                        return;
                    }

                    ServerCommunicator.sendCommandToServers(cmd);
                }
                @Override
                public String helpText()
                {
                    StringBuilder helpString = new StringBuilder("SEARCH <username>: ");
                    helpString.append("Returns all orders placed by <username>.");
                    return helpString.toString();
                }

            },

            /* LIST Command */
            new ShellCommand()
            {
                @Override
                public String CommandName()
                {
                    return "list";
                }

                @Override
                public void Execute(String cmd)
                {
                    /* There's no parameters needed for this command */
                    ServerCommunicator.sendCommandToServers(cmd);
                }

                @Override
                public String helpText()
                {
                    String helpString = new String("LIST: Lists all the avaliable items in stock.");
                    return helpString;
                }
            },

            /* CANCEL Command */
            new ShellCommand()
            {
                @Override
                public String CommandName()
                {
                    return "cancel";
                }

                @Override
                public void Execute(String cmd)
                {
                    String orderId = null;

                    String[] tokens = cmd.split(" ");
                    try {
                        orderId   = tokens[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("There was not enough parameters provided.");
                        System.out.print("\n>> ");
                        return;
                    }
                    ServerCommunicator.sendCommandToServers(cmd);
                }
                @Override
                public String helpText()
                {
                    StringBuilder helpString = new StringBuilder("CANCEL <orderid>: ");
                    helpString.append("If order <orderid> is present, the order is cancelled and quantity restocked.");
                    return helpString.toString();
                }

            },

            new ShellCommand()
            {
                @Override
                public String CommandName()
                {
                    return "help";
                }

                @Override
                public void Execute(String cmd)
                {
                    System.out.println("Commands: ");
                    for (ShellCommand a : commands) {
                        if (!a.CommandName().equals("help"))
                            System.out.println(a.helpText());
                    }
                    System.out.println(helpText());
                    System.out.print("\n>> ");
                }
                @Override
                public String helpText()
                {
                    StringBuilder helpString = new StringBuilder("HELP: ");
                    helpString.append("Prints out how to use every command.");
                    return helpString.toString();
                }


            }
    };

    public static void Interpreter()
    {
        System.out.print("\n>> ");
        boolean deadServer = false;
        String  previousContent = null;
        while (deadServer || StandardIOCommunicator.hasNextLine())
        {
            String cmd = StandardIOCommunicator.nextLine();
            String[] tokens = cmd.split(" ");
            Boolean commandMatched = false;
            for (ShellCommand avaliableCommand : commands) {
                try {
                    if (avaliableCommand.CommandName().equals(cmd.split(" ")[0]))
                    {
                        avaliableCommand.Execute(cmd);
                        commandMatched = true;
                    }
                } catch (Exception e) {
                    System.out.println("That was not a validly formatted command.");
                    System.out.print("\n>> ");
                    continue;
                }
            }

            if (!commandMatched)
            {
                System.out.println("No such command found.");
                System.out.print("\n>> ");
            }
            commandMatched = false;

    }

}
}
