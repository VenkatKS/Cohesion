import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Venkat on 4/5/16.
 */

// Allows implementation of Lamport's Mutual Exclusion Algorithm
public class MutualExclusionEnforcer
{
    static Comparator<TimeStamp>    timeStampComparator     = new TimeStampComparator();

    static PriorityQueue<TimeStamp> requestQueue            = new PriorityQueue<TimeStamp>(100, timeStampComparator);

    static Semaphore                PriorityQueueSemaphore  = new Semaphore(1);

    static AtomicInteger            receivedAck             = new AtomicInteger(0);

    static TimeStamp                activeRequest           = null;

    /* Condition Variable Synchronization */
    private static final Object lock = new Object();

    /* NOTE: This is a call to enter the CS. This will block the thread until the CS is ready. */
    public static void requestCriticalSection()
    {
        synchronized (lock)
        {
            /* NOTE: Tick the causal clock, as per the algorithm */
            CurrentNodeProperties.currentClock.tick();

            /* NOTE: Bundle the current clock and the request type */
            CausalClock frozenClock = new CausalClock(CurrentNodeProperties.currentClock.get());  /* Freeze the time right here */

            Message requestMsg = new Message(new CausalClock(frozenClock.get()), Message.MESSAGE_MUTEX_REQUEST, CurrentNodeProperties.nodeID);
            TimeStamp requestStamp = new TimeStamp(CurrentNodeProperties.nodeID, new CausalClock(frozenClock.get()));

            /* NOTE: Add our request to the waiting queue */
            while (true)
            {
                try
                {
                    PriorityQueueSemaphore.acquire();
                    break;
                } catch (InterruptedException e)
                {
                    continue;
                }
            }

            requestQueue.add(requestStamp);
            activeRequest = requestStamp;

            PriorityQueueSemaphore.release();

            Networking.broadcastMessage(requestMsg);
            /* NOTE: Block until we get every ack back, and we are the top of the list */
            /* TODO: Fault tolerance */

            while (receivedAck.get() < (Graph.getNumberOfActiveCommunications() - 1) || requestQueue.peek().pid != CurrentNodeProperties.nodeID)
            {
                try
                {
                    lock.wait();
                } catch (InterruptedException e)
                {
                    continue;
                }
            }
        }
    }

    public static void OnReceive(Message rxMsg)
    {
        CurrentNodeProperties.currentClock.newClock(new CausalClock(rxMsg.getClockValue())); /* Max of clocks */

        if (rxMsg.getMessageType() == Message.MESSAGE_MUTEX_REQUEST) /* A request message */
        {
            synchronized (lock)
            {
                /* NOTE: Bundle the current clock and the request type */
                CausalClock frozenClock = new CausalClock(CurrentNodeProperties.currentClock.get());  /* Freeze the time right here */
                Message ackMsg = new Message(new CausalClock(frozenClock.get()), Message.MESSAGE_MUTEX_ACK, CurrentNodeProperties.nodeID);

                TimeStamp requestStamp = new TimeStamp(rxMsg.getSenderID(), new CausalClock(rxMsg.getClockValue()));
                /* NOTE: Add our request to the waiting queue */
                while (true)
                {
                    try
                    {
                        PriorityQueueSemaphore.acquire();
                        break;
                    } catch (InterruptedException e)
                    {
                        continue;
                    }
                }

                requestQueue.add(requestStamp);

                PriorityQueueSemaphore.release();

                Networking.sendToNode(ackMsg, rxMsg.getSenderID());
            }

        } else if (rxMsg.getMessageType() == Message.MESSAGE_MUTEX_ACK)
        {
            synchronized (lock)
            {
                receivedAck.incrementAndGet();
                lock.notify();
            }
        }
        else if (rxMsg.getMessageType() == Message.MESSAGE_MUTEX_RELEASE)
        {
            /* NOTE : Apply the necessary operations */
            if (rxMsg.Operation == Message.OPERATION_TYPE_PURCHASE)
            {
                try
                {
                    DistributedInventoryManager.executeOrder(rxMsg.userName, rxMsg.productName, rxMsg.Parameter);
                }
                catch (DistributedInventoryManager.NoSuchItem noSuchItem)
                {
                    /* NOTE: This shouldn't really be an issue due to it succeeding on the other end. */
                }
                catch (DistributedInventoryManager.NotEnoughItems notEnoughItems)
                {
                    /* NOTE: This shouldn't really be an issue due to FIFO. */
                }
            }
            else if (rxMsg.Operation == Message.OPERATION_TYPE_CANCEL)
            {
                    DistributedInventoryManager.ReverseOrder(rxMsg.Parameter);
            }

            synchronized (lock)
            {

                while (true)
                {
                    try
                    {
                        PriorityQueueSemaphore.acquire();
                        break;
                    } catch (InterruptedException e)
                    {
                        continue;
                    }
                }

                /* Delete the releasing process's request stamp */
                Iterator<TimeStamp> allStamps = requestQueue.iterator();

                while (allStamps.hasNext())
                {

                    /* Iterate through all the stamps, find your stamp, and remove it from the request queue. */
                    TimeStamp nextStamp = allStamps.next();

                    if (nextStamp.pid == rxMsg.getSenderID())
                    {
                        allStamps.remove(); /* Remove the last retrieved element. */
                        lock.notify();
                    }

                }
                PriorityQueueSemaphore.release();
            }
        }
    }

    public static void releaseCriticalSection(int Operation, String userName, String product, int Parameter)
    {
        /* NOTE: If we're not in the critical section, we don't need to do anything and just return. */
        if (activeRequest == null) return;
        synchronized (lock)
        {
            CausalClock frozenClock = new CausalClock(CurrentNodeProperties.currentClock.get());  /* Freeze the time right here */
            Message relMsg = new Message(new CausalClock(frozenClock.get()), Message.MESSAGE_MUTEX_RELEASE, CurrentNodeProperties.nodeID);

            /* Set all the necessary operations to be performed on the rx side */
            relMsg.Operation    =   Operation;
            relMsg.userName     =   userName;
            relMsg.productName  =   product;
            relMsg.Parameter    =   Parameter;
            relMsg.setOrderNumbers(DistributedInventoryManager.OrderSystem.getUniqueId());

            /* NOTE: Remove your request from your own queue. */
            while (true)
            {
                try
                {
                    PriorityQueueSemaphore.acquire();
                    break;
                } catch (InterruptedException e)
                {
                    continue;
                }
            }

            requestQueue.remove(activeRequest);
            activeRequest = null; /* We're done with the request */
            PriorityQueueSemaphore.release();


            Networking.broadcastMessage(relMsg);

        }
    }
}
