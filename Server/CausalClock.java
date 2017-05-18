import java.io.Serializable;

/**
 * Created by Venkat on 4/5/16.
 */
public class CausalClock implements Serializable
{
    private int clockValue;

    public CausalClock(int initialVal)
    {
        clockValue = initialVal;
    }

    public synchronized void tick()
    {
        clockValue++;
    }

    public synchronized void newClock(CausalClock receivedClock)
    {
        clockValue = Math.max(clockValue, receivedClock.clockValue);
        clockValue++;
    }

    public synchronized int get()
    {
        return clockValue;
    }
}
