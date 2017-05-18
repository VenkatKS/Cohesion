import java.util.Comparator;

/**
 * Created by Venkat on 4/6/16.
 */
public class TimeStampComparator implements Comparator<TimeStamp>
{
    @Override
    public int compare(TimeStamp o1, TimeStamp o2)
    {
        if (o1.clockTime.get() < o2.clockTime.get()) return -1;
        else if (o1.clockTime.get() > o2.clockTime.get()) return 1;
        else
        {
            if (o1.pid < o2.pid) return -1;
            else return 1;
        }
    }
}
