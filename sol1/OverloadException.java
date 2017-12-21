/**
 * An exception that occurs when a bicycle is damaged.:
 */

@SuppressWarnings("serial")
public class OverloadException extends HandlingException {
    /**
     * Create a new OverloadException with a specified message.
     */
    public OverloadException(String message) {
        super(message);
    }
}
