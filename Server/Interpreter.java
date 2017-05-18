import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Created by Venkat on 2/26/16.
 */
public class Interpreter
{
    String myDelimiter = new String(" ");

    public Interpreter()
    {

    }

    public String executeCommand(String unformattedCommand) throws IllegalCommand
    {
        StringTokenizer CommandTokens = new StringTokenizer(unformattedCommand, myDelimiter);
        String Command = null;
        try
        {
            Command = CommandTokens.nextToken();
        }
        catch(NoSuchElementException e)
        {
            throw new IllegalCommand("Empty command.", 1);
        }

        if (Command == null)
        {
            throw new IllegalCommand("Empty command.", 1);
        }

        if (Command.equals("purchase"))
        {
            String userName = null;
            String prodName = null;
            String quantity = null;
            try {
                userName = CommandTokens.nextToken();
                prodName = CommandTokens.nextToken();
                quantity = CommandTokens.nextToken();
            }
            catch(NoSuchElementException e)
            {
                throw new IllegalCommand("Not enough params", 3);
            }

            DistributedOrderManager.Order newOrder = null;

            try
            {
                MutualExclusionEnforcer.requestCriticalSection();   /* NOTE: Ask permission to edit the distributed data structure */
                newOrder = DistributedInventoryManager.executeOrder(userName, prodName, Integer.parseInt(quantity));
                MutualExclusionEnforcer.releaseCriticalSection(Message.OPERATION_TYPE_PURCHASE, userName, prodName, Integer.parseInt(quantity)); /* NOTE: Let everyone else know about the changes made */
            }
            catch(DistributedInventoryManager.NoSuchItem e)
            {
                MutualExclusionEnforcer.releaseCriticalSection(Message.OPERATION_TYPE_NOCHANGE, userName, prodName, Integer.parseInt(quantity)); /* NOTE: Let everyone else know no changes */
                return "Not Available - We do not sell this product";
            }
            catch(DistributedInventoryManager.NotEnoughItems g)
            {
                MutualExclusionEnforcer.releaseCriticalSection(Message.OPERATION_TYPE_NOCHANGE, userName, prodName, Integer.parseInt(quantity)); /* NOTE: Let everyone else know no changes */
                return "Not Available - Not enough items";
            }
            catch(NumberFormatException h)
            {
                MutualExclusionEnforcer.releaseCriticalSection(Message.OPERATION_TYPE_NOCHANGE, userName, prodName, Integer.parseInt(quantity)); /* NOTE: Let everyone else know no changes */
                return "Illegal number specified";
            }

            return "You order has been placed, " + newOrder.OrderId + " " +  newOrder.UserOrder.myName + " " + newOrder.ProductName + " " + newOrder.Quantity;
        }
        else if (Command.equals("cancel"))
        {
            String orderId = null;
            try
            {
                orderId = CommandTokens.nextToken();
            }
            catch(NoSuchElementException e)
            {
                throw new IllegalCommand("Not enough params", 3);
            }

            try
            {
                MutualExclusionEnforcer.requestCriticalSection();   /* NOTE: Ask permission to edit the distributed data structure */
                boolean result = DistributedInventoryManager.ReverseOrder(Integer.parseInt(orderId));
                if (result)
                {
                    MutualExclusionEnforcer.releaseCriticalSection(Message.OPERATION_TYPE_CANCEL, null, null, Integer.parseInt(orderId)); /* NOTE: Let everyone else know about the changes made */
                    return "Order " + orderId + " is canceled";
                }
                else
                {
                    MutualExclusionEnforcer.releaseCriticalSection(Message.OPERATION_TYPE_NOCHANGE, null, null, 0); /* NOTE: Let everyone else know no changes */
                    return orderId + " not found, no such order";
                }
            }
            catch (NumberFormatException h)
            {
                MutualExclusionEnforcer.releaseCriticalSection(Message.OPERATION_TYPE_NOCHANGE, null, null, 0); /* NOTE: Let everyone else know no changes */
                return "Illegal number specified";
            }

        }
        else if (Command.equals("search"))
        {
            String userName = null;
            try
            {
                userName = CommandTokens.nextToken();
            }
            catch(NoSuchElementException e)
            {
                throw new IllegalCommand("Not enough params", 3);
            }

            ArrayList<DistributedOrderManager.Order> UserOrders = DistributedInventoryManager.GetCurrentUserOrders(userName);

            if (UserOrders == null || UserOrders.size() == 0)
            {
                return "No order found for " + userName;
            }
            else
            {
                StringBuilder finalReturn = new StringBuilder();

                for(int i = 0; i < UserOrders.size(); i++)
                {
                    DistributedOrderManager.Order nextOrder = UserOrders.get(i);
                    finalReturn.append(nextOrder.OrderId);
                    finalReturn.append(", ");
                    finalReturn.append(nextOrder.ProductName);
                    finalReturn.append(", ");
                    finalReturn.append(nextOrder.Quantity);
                    if (i < UserOrders.size() - 1) finalReturn.append("\n");
                }

                return finalReturn.toString();
            }

        }
        else if (Command.equals("list"))
        {
            // TODO: List
            StringBuilder finalReturn = new StringBuilder();

            ArrayList<Inventory> myItems = DistributedInventoryManager.getAllInventoryItems();

            for(int i = 0; i < myItems.size(); i++)
            {
                Inventory nextItem = myItems.get(i);
                finalReturn.append(nextItem.getMyName());
                finalReturn.append(" ");
                finalReturn.append(nextItem.getMyQuantity());
                if (i < myItems.size() - 1) finalReturn.append("\n");
            }

            return finalReturn.toString();
        }
        else
        {
            throw new IllegalCommand("Illegal Command", 2);
        }

    }

    public class IllegalCommand extends Exception
    {
        int errornumber = 0;
        public IllegalCommand(int number) { super(); errornumber = number;}
        public IllegalCommand(String message, int number) { super(message); errornumber = number;}
        public IllegalCommand(String message, Throwable cause) { super(message, cause); }
        public IllegalCommand(Throwable cause) { super(cause); }
    }



}
