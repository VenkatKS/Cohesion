import java.io.Serializable;

/**
 * Created by Venkat on 4/9/16.
 */
public class Inventory implements Serializable
{
    private String  myName;
    private int     myQuantity;

    public Inventory(String name, int quantity)
    {
        myName = name;
        myQuantity = quantity;
    }

    public String getMyName()
    {
        return myName;
    }

    public void setMyName(String myName)
    {
        this.myName = myName;
    }

    public int getMyQuantity()
    {
        return myQuantity;
    }

    public void setMyQuantity(int myQuantity)
    {
        this.myQuantity = myQuantity;
    }

    public String toString()
    {
        return myName + " :: " + Integer.toString(myQuantity);
    }

    @Override
    public int hashCode()
    {
        return myName.hashCode();
    }
}
