/**
 * Created by Venkat on 6/3/17.
 */
public enum ErrorCodes
{
    INVALID_USER_INPUT(-1);

    private int numVal;

    ErrorCodes(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }

}
