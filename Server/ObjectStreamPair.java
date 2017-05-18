import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.concurrent.Semaphore;

/**
 * Created by Venkat on 4/6/16.
 */
public class ObjectStreamPair
{
    private ObjectOutputStream  OutputStream;
    private Semaphore           StreamMutex;


    public ObjectStreamPair(ObjectOutputStream newStream)
    {
        OutputStream    = newStream;
        StreamMutex     = new Semaphore(1);
    }

    // Blocking call for stream
    /*
        @param: none
        @postcondition: MUST call releaseSocket after socket is done
     */
    public ObjectOutputStream acquireStream() throws InterruptedException
    {
        StreamMutex.acquire();
        return OutputStream;
    }

    public void releaseStream()
    {
        StreamMutex.release();
    }

}
