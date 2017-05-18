import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by Venkat on 2/26/16.
 */
public class InventoryParser
{
    public String myFileName;
    BufferedReader myReader = null;
    String myDelimiter = " ";

    public InventoryParser(String filename) throws FileNotFoundException
    {
        myFileName = filename;
        myReader = new BufferedReader(new FileReader(filename));
    }

    public InventoryParser(String filename, String delimiter) throws FileNotFoundException
    {
        myFileName = filename;
        myReader = new BufferedReader(new FileReader(filename));
        myDelimiter = delimiter;
    }

    public Inventory getNextItem() throws IOException, OutOfItems {
        String readerCurrentLine = myReader.readLine();

        if (readerCurrentLine == null)
        {
            throw new OutOfItems("Reader has reached end of inventory file.");
        }

        StringTokenizer LineTokenizer = new StringTokenizer(readerCurrentLine, myDelimiter);

        if (readerCurrentLine.equals(""))
        {
            return null;
        }
        else if (LineTokenizer.countTokens() != 2)
        {
            return null;
        }

        return new Inventory(LineTokenizer.nextToken(), Integer.parseInt(LineTokenizer.nextToken()));

    }






    public class OutOfItems extends Exception
    {
        // The reader has reached the end of file
        public OutOfItems() { super(); }
        public OutOfItems(String message) { super(message); }
        public OutOfItems(String message, Throwable cause) { super(message, cause); }
        public OutOfItems(Throwable cause) { super(cause); }

    }



}

