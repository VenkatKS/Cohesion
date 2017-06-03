/**
 * Created by Venkat on 6/3/17.
 */
public interface ShellCommand
{
    String CommandName();
    String helpText();
    void Execute(String cmd);
}
