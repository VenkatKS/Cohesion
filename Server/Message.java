import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Venkat on 4/5/16.
 */
public class Message implements Serializable
{
    public static final int MESSAGE_MUTEX_REQUEST   = 0;
    public static final int MESSAGE_MUTEX_ACK       = 1;
    public static final int MESSAGE_MUTEX_RELEASE   = 2;

    public static final int OPERATION_TYPE_PURCHASE = 0;
    public static final int OPERATION_TYPE_CANCEL   = 1;
    public static final int OPERATION_TYPE_NOCHANGE = 2;

    private CausalClock messageClock;
    private int messageType;

    private int pid;


    int                     Operation;
    String                  userName;
    String                  productName;
    int                     Parameter;      // Either quantity, or order number

    private AtomicInteger   OrderNumbers;



    public AtomicInteger getOrderNumbers()
    {
        return OrderNumbers;
    }

    public void setOrderNumbers(AtomicInteger orderNumbers)
    {
        OrderNumbers = orderNumbers;
    }



    /* NOTE: For Lamport's Mutex Algo, regardless of the type of message, we need to transmit the clock as well */
    public Message(CausalClock currentClock, int messageType, int pid)
    {
        this.messageType    = messageType;
        this.messageClock   = currentClock;
        this.pid            = pid;
    }

    public int getClockValue()
    {
        return messageClock.get();
    }

    public int getMessageType()
    {
        return messageType;
    }

    public int getSenderID()
    {
        return pid;
    }

}
