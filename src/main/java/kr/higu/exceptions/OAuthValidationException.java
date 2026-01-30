package kr.higu.exceptions;

/**
 * Exception thrown when there is a validation error in the request parameters or configuration.
 * <p>
 * This is a {@link RuntimeException} because it usually indicates a developer error
 * (e.g., forgetting to set a required field in the builder) rather than a recoverable runtime issue.
 * </p>
 * * @author higukang
 */
public class OAuthValidationException extends RuntimeException {

    /**
     * Constructs a new OAuthValidationException with the specified detail message.
     *
     * @param message The detail message explaining the validation failure.
     */
    public OAuthValidationException(String message) {
        super(message);
    }
}