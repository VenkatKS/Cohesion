import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Venkat on 4/9/16.
 */
public class DistributedInventoryManager
{
    static HashMap<String, Inventory>   ServerInventory = new HashMap<String, Inventory>();
    static DistributedOrderManager      OrderSystem     = new DistributedOrderManager();


    static DistributedInventoryManager                  anchorItem      = new DistributedInventoryManager();
    /* NOTE: Only call this when trying to initialize the system. Don't call this after the first client has been added */
    public static synchronized void putItem(String itemName, Inventory item)
    {
        //System.out.println(itemName);
        ServerInventory.put(itemName, item);
    }

    public static synchronized DistributedOrderManager.Order executeOrder(String userName, String productName, int quantity) throws NoSuchItem, NotEnoughItems
    {
        Inventory updatingItem = ServerInventory.get(productName);
        if (updatingItem == null)
        {
            throw anchorItem. new NoSuchItem();
        }

        if (updatingItem.getMyQuantity() - quantity < 0)
        {
            throw anchorItem.new NotEnoughItems();
        }

        updatingItem.setMyQuantity(updatingItem.getMyQuantity() - quantity);

        DistributedOrderManager.Order returnVal = OrderSystem.RegisterNewOrder(userName, productName, quantity);

        return returnVal;
    }

    public static synchronized boolean ReverseOrder(int OrderNumber)
    {
        DistributedOrderManager.Order findOrder = OrderSystem.getSpecificOrder(OrderNumber);
        if (findOrder == null)
        {
            MutualExclusionEnforcer.releaseCriticalSection(Message.OPERATION_TYPE_CANCEL, null, null, OrderNumber); /* NOTE: Let everyone else know about the changes made */
            return false;
        }

        int quantity = findOrder.Quantity;
        String item  = findOrder.ProductName;

        Inventory ItemToUpdate = ServerInventory.get(item);
        ItemToUpdate.setMyQuantity(ItemToUpdate.getMyQuantity() + quantity);
        boolean success = OrderSystem.deleteSpecificOrder(OrderNumber);
        if (Server.DEBUG) System.out.println("Reversed order: " + success + " at time: " + System.currentTimeMillis());
        assert(success == true);


        return true;
    }

    public static synchronized ArrayList<Inventory> getAllInventoryItems()
    {
        ArrayList<Inventory> AllItems = new ArrayList<Inventory>(ServerInventory.values());
        return AllItems;
    }


    public static ArrayList<DistributedOrderManager.Order> GetCurrentUserOrders(String userName)
    {
        return OrderSystem.getUserOrders(userName);
    }

    public static synchronized void updateAll(HashMap<String, Inventory> ParamServerInventory, HashMap<String, ArrayList<DistributedOrderManager.Order>> AllUsers, HashMap<DistributedOrderManager.Order, DistributedOrderManager.User> AllOrders, HashMap<Integer, DistributedOrderManager.Order> OrderReg, AtomicInteger orderValuesCurrent)
    {
        ServerInventory   = new HashMap<String, Inventory>(ParamServerInventory);
        OrderSystem.updateAll(AllUsers, AllOrders, OrderReg);
        OrderSystem.setUniqueId(orderValuesCurrent);
        if (Server.DEBUG) System.out.println("Updated at time: " + System.currentTimeMillis());


    }

    public class NoSuchItem extends Exception
    {

    }
    public class NotEnoughItems extends Exception
    {
    }


}
