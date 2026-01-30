package kr.higu;

import kr.higu.exceptions.OAuthException;
import kr.higu.exceptions.detailed.OAuthInterruptedException;
import kr.higu.exceptions.detailed.OAuthNetworkException;
import kr.higu.exceptions.detailed.OAuthResponseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Default implementation of {@link IHttpManager} using Java's built-in {@link HttpClient}.
 * <p>
 * This class is implemented as a thread-safe Singleton.
 * <p>
 * It handles the execution of HTTP requests, manages timeouts,
 * and translates lower-level network exceptions into library-specific {@link OAuthException}s.
 * </p>
 *
 * @author higukang
 */
public class OAuthHttpManager implements IHttpManager {
    private static final OAuthHttpManager INSTANCE = new OAuthHttpManager();
    private final HttpClient httpClient;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Configures the internal {@link HttpClient} with a 10-second connect timeout
     * and normal redirect following policy.
     */
    private OAuthHttpManager() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Returns the singleton instance of {@code OAuthHttpManager}.
     *
     * @return The singleton instance.
     */
    public static OAuthHttpManager getInstance() {
        return INSTANCE;
    }

    @Override
    public String get(URI uri, Map<String, String> headers) throws OAuthException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(10))
                .GET();
        if (headers != null) headers.forEach(builder::header);

        return execute(builder.build());
    }

    @Override
    public String post(URI uri, Map<String, String> headers, String body) throws OAuthException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
        if (headers != null) headers.forEach(builder::header);

        return execute(builder.build());
    }

    /**
     * Executes the given {@link HttpRequest} and handles the {@link HttpResponse}.
     * @param request The prepared HTTP request to send.
     * @return The response body as a String if the status code is 2xx.
     * @throws OAuthException If the server returns a non-2xx status code or if the thread is interrupted.
     * @throws OAuthNetworkException If an IOException occurs during network communication.
     */
    private String execute(HttpRequest request) throws OAuthException {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            // 2xx Success range
            if (response.statusCode() >= 200 && response.statusCode() <= 300) {
                return body;
            } else {
                throw new OAuthResponseException(
                        response.statusCode(),
                        null,
                        body,
                        "OAuth server returned an error."
                );
            }
        } catch (IOException e) {
            throw new OAuthNetworkException("Failed to connect to the OAuth server." + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OAuthInterruptedException("Request to the OAuth server was interrupted." + e.getMessage(), e);
        }
    }
}
