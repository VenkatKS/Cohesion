import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Venkat on 4/9/16.
 */
public class LocalInventoryFunctions
{



    public static void StartUpdatingInventory(String invFilePath)
    {
        InventoryParser inventoryFileParser = null;
        try
        {
            inventoryFileParser = new InventoryParser(invFilePath);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Inventory file was not found.");
            System.exit(-1);
        }

        while(true)
        {
            try /* NOTE: Attempt to read in the next item from the inventory file. */
            {
                Inventory nextItem = inventoryFileParser.getNextItem();
                if (nextItem != null)
                {
                    DistributedInventoryManager.putItem(nextItem.getMyName(), nextItem);
                }
            }
            catch(InventoryParser.OutOfItems SystemOutOfItems)
            {
                break;
            }
            catch(IOException errFile)
            {
                System.out.println("IO Exception while reading inventory file.");
                System.exit(-1);
            }
        }
    }


}
