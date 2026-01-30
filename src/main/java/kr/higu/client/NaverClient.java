package kr.higu.client;

import kr.higu.IHttpManager;
import kr.higu.OAuthHttpManager;
import kr.higu.request.naver.NaverTokenRequest;
import kr.higu.request.naver.NaverUserRequest;

/**
 * The main entry point for interacting with Naver OAuth services.
 * <p>
 * This client provides access to Naver-specific request builders,
 * allowing developers to easily exchange authorization codes for tokens
 * and retrieve user profile information.
 * </p>
 *
 * <pre>{@code
 * NaverClient client = NaverClient.create();
 * NaverTokenResponse token = client.getToken()
 * .clientId("YOUR_CLIENT_ID")
 * .clientSecret("YOUR_CLIENT_SECRET")
 * .code("AUTHORIZATION_CODE")
 * .state("YOUR_STATE")
 * .build()
 * .execute();
 * }</pre>
 *
 * @author higukang
 */
public class NaverClient {
    private final IHttpManager httpManager;

    /**
     * Internal constructor for NaverClient.
     * Use {@link #create()} or {@link #create(IHttpManager)} to instantiate.
     *
     * @param httpManager The HTTP manager to be used for all requests initiated by this client.
     */
    private NaverClient(IHttpManager httpManager) {
        this.httpManager = httpManager;
    }

    /**
     * Creates a new NaverClient instance using the default {@link OAuthHttpManager}.
     *
     * @return A new NaverClient instance.
     */
    public static NaverClient create() {
        return new NaverClient(OAuthHttpManager.getInstance());
    }

    /**
     * Creates a new NaverClient instance with a custom {@link IHttpManager}.
     * This is useful for testing purposes or for providing a specific HTTP configuration.
     *
     * @param httpManager A custom implementation of IHttpManager.
     * @return A new NaverClient instance.
     */
    public static NaverClient create(IHttpManager httpManager) {
        return new NaverClient(httpManager);
    }

    /**
     * Provides a builder for creating a {@link NaverTokenRequest}.
     * Use this to perform the authorization code exchange with Naver's auth server.
     *
     * @return A builder for NaverTokenRequest.
     */
    public NaverTokenRequest.Builder getToken() {
        return new NaverTokenRequest.Builder(httpManager);
    }

    /**
     * Provides a builder for creating a {@link NaverUserRequest}.
     * Use this to retrieve the user's profile details using a valid access token.
     *
     * @return A builder for NaverUserRequest.
     */
    public NaverUserRequest.Builder getUserInfo() {
        return new NaverUserRequest.Builder(httpManager);
    }
}
