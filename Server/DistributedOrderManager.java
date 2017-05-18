import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Venkat on 4/9/16.
 */
public class DistributedOrderManager implements Serializable
{
    HashMap<String, ArrayList<Order>>    AllUsers    = new HashMap<String, ArrayList<Order>>();
    HashMap<Order, User>                 AllOrders   = new HashMap<Order, User>();
    HashMap<Integer, Order>              OrderReg    = new HashMap<Integer, Order>();
    private AtomicInteger                UniqueId    = new AtomicInteger(1000000);





    public void setUniqueId(AtomicInteger uniqueId)
    {
        UniqueId = new AtomicInteger(uniqueId.get());
    }


    public AtomicInteger getUniqueId()
    {
        return UniqueId;
    }


    public synchronized Order RegisterNewOrder(String userName, String productName, int quantity) // also registers new users
    {
        User currentUser = new User(userName);

        ArrayList<Order> UsersOrders = AllUsers.get(userName);

        if (UsersOrders == null)
        {
            UsersOrders = new ArrayList<Order>();
            AllUsers.put(userName, UsersOrders); // add new user
        }

        Order CurrentOrder = new Order(generateOrderId(), productName, quantity, currentUser);
        UsersOrders.add(CurrentOrder);
        AllOrders.put(CurrentOrder, currentUser);
        OrderReg.put(CurrentOrder.OrderId, CurrentOrder);
        return CurrentOrder;
    }

    public void updateAll(HashMap<String, ArrayList<Order>> AllUsers, HashMap<Order, User> AllOrders, HashMap<Integer, Order> OrderReg)
    {
        this.AllUsers   = new HashMap<String, ArrayList<Order>>(AllUsers);
        this.AllOrders  = new HashMap<Order, User>(AllOrders);
        this.OrderReg   = new HashMap<Integer, Order>(OrderReg);
    }

    public Order getSpecificOrder(int oid)
    {
        return OrderReg.get(new Integer(oid));
    }

    public boolean deleteSpecificOrder(int oid)
    {
        Order orderInQuestion = OrderReg.get(oid);
        if (orderInQuestion == null) return false;
        OrderReg.remove(oid);

        User userInQuestion = AllOrders.get(orderInQuestion);
        if (userInQuestion == null) return false;
        AllOrders.remove(orderInQuestion);

        ArrayList<Order> UsersOrders = AllUsers.get(userInQuestion.myName);
        UsersOrders.remove(orderInQuestion);

        return true;

    }

    public ArrayList<Order> getUserOrders(String userName)
    {
        User newPerson = new User(userName);

        return AllUsers.get(userName);
    }

    public synchronized User getUser(Order getUserFor)
    {
        return AllOrders.get(getUserFor);
    }

    private int generateOrderId()
    {
        return UniqueId.getAndIncrement();
    }



    public class Order implements Serializable
    {
        public int OrderId;
        public String ProductName;
        public int Quantity;
        public User UserOrder;

        public Order(int oid)
        {
            OrderId = oid;
        }

        public Order(int oid, String prod, int quant, User userWhoBought)
        {
            OrderId = oid;
            ProductName = prod;
            Quantity = quant;
            UserOrder = userWhoBought;
        }

        @Override
        public int hashCode()
        {
            return OrderId;
        }
    }

    public class User implements Serializable
    {
        String myName;

        public User(String UserName)
        {
            myName = UserName;
        }

        @Override
        public int hashCode()
        {
            return myName.hashCode();
        }

    }



}
