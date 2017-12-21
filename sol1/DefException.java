/**
 * An exception that occurs when a defective bicycle makes it to the end of
 * the belt without being inspected
 */
@SuppressWarnings("serial")
public class DefException extends HandlingException {
    /**
     * Create a new DefException
     */
    public DefException(String message) {
        super(message);
    }
}
