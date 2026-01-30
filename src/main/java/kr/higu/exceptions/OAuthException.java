package kr.higu.exceptions;

/**
 * Base exception class for all errors that occur within the K-OAuth library.
 * <p>
 * This is a checked exception, meaning users of the library are expected to
 * handle potential OAuth-related failures (e.g., network issues, provider errors).
 * All specific exceptions in this library extend this class.
 * </p>
 *
 * @author higukang
 */
public class OAuthException extends Exception {
    /**
     * Constructs a new OAuthException with the specified detail message.
     *
     * @param message The detail message explaining the reason for the exception.
     */
    public OAuthException(String message) {
        super(message);
    }

    /**
     * Constructs a new OAuthException with the specified detail message and cause.
     *
     * @param message The detail message explaining the reason for the exception.
     * @param cause   The underlying cause of the exception (e.g., an IOException).
     */
    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
