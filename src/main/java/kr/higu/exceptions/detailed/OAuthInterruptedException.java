package kr.higu.exceptions.detailed;

import kr.higu.exceptions.OAuthException;

/**
 * Exception thrown when a thread executing an OAuth request is interrupted.
 * <p>
 * This typically occurs when the request is canceled or the application is
 * shutting down while waiting for a response from the OAuth provider.
 * </p>
 *
 * @author higukang
 */
public class OAuthInterruptedException extends OAuthException {
    /**
     * Constructs a new OAuthInterruptedException with a detail message and the underlying cause.
     *
     * @param message The detail message explaining the context of the interruption.
     * @param cause   The original {@link InterruptedException} that triggered this exception.
     */
    public OAuthInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
