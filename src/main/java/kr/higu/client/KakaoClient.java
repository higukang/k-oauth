package kr.higu.client;

import kr.higu.IHttpManager;
import kr.higu.OAuthHttpManager;
import kr.higu.request.kakao.KakaoTokenRequest;
import kr.higu.request.kakao.KakaoUserRequest;

/**
 * The main entry point for interacting with Kakao OAuth services.
 * <p>
 * This client provides access to various Kakao-specific request builders,
 * such as token exchange and user information retrieval.
 * It uses a fluent API style through builders to make OAuth integration simple and readable.
 * </p>
 *
 * <pre>{@code
 * KakaoClient client = KakaoClient.create();
 * KakaoTokenResponse token = client.getToken()
 * .clientId("REST_API_KEY")
 * .redirectUri("REDIRECT_URI")
 * .code("AUTHORIZATION_CODE")
 * .clientSecret("CLIENT_SECRET") //optional
 * .build()
 * .execute();
 * }</pre>
 *
 * @author higukang
 */
public class KakaoClient {
    private final IHttpManager httpManager;

    /**
     * Internal constructor for KakaoClient.
     * Use {@link #create()} or {@link #create(IHttpManager)} to instantiate.
     *
     * @param httpManager The HTTP manager to be used for all requests initiated by this client.
     */
    private KakaoClient(IHttpManager httpManager) {
        this.httpManager = httpManager;
    }

    /**
     * Creates a new KakaoClient instance using the default {@link OAuthHttpManager}.
     *
     * @return A new KakaoClient instance.
     */
    public static KakaoClient create() {
        return new KakaoClient(OAuthHttpManager.getInstance());
    }

    /**
     * Creates a new KakaoClient instance with a custom {@link IHttpManager}.
     * Useful for testing with mock managers or using custom HTTP configurations.
     *
     * @param httpManager A custom implementation of IHttpManager.
     * @return A new KakaoClient instance.
     */
    public static KakaoClient create(IHttpManager httpManager) {
        return new KakaoClient(httpManager);
    }

    /**
     * Provides a builder for creating a {@link KakaoTokenRequest}.
     * This is used for the Authorization Code Flow to exchange a code for tokens.
     *
     * @return A builder for KakaoTokenRequest.
     */
    public KakaoTokenRequest.Builder getToken() {
        return new KakaoTokenRequest.Builder(httpManager);
    }

    /**
     * Provides a builder for creating a {@link KakaoUserRequest}.
     * This is used to retrieve profile and account details using an access token.
     *
     * @return A builder for KakaoUserRequest.
     */
    public KakaoUserRequest.Builder getUserInfo() {
        return new KakaoUserRequest.Builder(httpManager);
    }
}
