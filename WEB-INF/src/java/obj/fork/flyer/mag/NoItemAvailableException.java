package obj.fork.flyer.mag;

public class NoItemAvailableException extends Exception
{
    private String msg = "There are not Items found in channel";

    public NoItemAvailableException()
    {
    }

    public NoItemAvailableException(String message)
    {
        msg = message;
    }

    public String getMessage()
    {
        return msg;
    }
}
