import java.util.Scanner;

/**
 * Created by Venkat on 6/3/17.
 */
public class StandardIOCommunicator
{
    private static Scanner sc = new Scanner(System.in);

    public static int getNextInteger()
    {
        int value = -1;
        try {
            value = sc.nextInt();
        } catch (Exception e) {
            System.err.print("Please only enter a valid number.");
            System.exit(ErrorCodes.INVALID_USER_INPUT.getNumVal());
        }
        return value;
    }

    public static Boolean hasNextLine()
    {
        return sc.hasNextLine();
    }

    public static String nextLine()
    {
        return sc.nextLine();
    }
}
