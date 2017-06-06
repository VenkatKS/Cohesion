
/**
 * Created by Venkat on 4/5/16.
 */
public class TimeStamp
{
    int pid;
    CausalClock clockTime;

    public TimeStamp(int pid, CausalClock clockTime)
    {
        this.pid        = pid;
        this.clockTime  = clockTime;
    }

}
