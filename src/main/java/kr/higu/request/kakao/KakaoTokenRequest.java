package kr.higu.request.kakao;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.higu.IHttpManager;
import kr.higu.dto.kakao.KakaoTokenResponse;
import kr.higu.request.AbstractRequest;
import kr.higu.request.ErrorDetail;

import java.net.URI;

/**
 * Request class for exchanging an authorization code for a Kakao access token.
 * This class follows the OAuth 2.0 Authorization Code Flow.
 *
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token">Kakao Token Request API Documentation</a>
 * @author higukang
 */
public class KakaoTokenRequest extends AbstractRequest<KakaoTokenResponse> {

    private KakaoTokenRequest(Builder builder) {
        super(builder);
    }

    /**
     * Builder for creating {@link KakaoTokenRequest} instances.
     */
    public static class Builder extends AbstractRequest.Builder<KakaoTokenResponse, Builder> {

        /**
         * Initializes the builder with default headers and the mandatory grant_type.
         *
         * @param httpManager The HTTP manager to use for the request.
         */
        public Builder(IHttpManager httpManager) {
            super(httpManager, KakaoTokenResponse.class);
            this.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            this.addParam("grant_type", "authorization_code");
        }

        /**
         * Sets the REST API Key of your Kakao application.
         *
         * @param clientId The REST API Key.
         * @return This builder instance.
         */
        public Builder clientId(String clientId) {
            return addParam("client_id", clientId);
        }

        /**
         * Sets the Redirect URI registered in the Kakao Developer Console.
         *
         * @param redirectUri The registered redirect URI.
         * @return This builder instance.
         */
        public Builder redirectUri(String redirectUri) {
            return addParam("redirect_uri", redirectUri);
        }

        /**
         * Sets the authorization code received from the Kakao login process.
         *
         * @param code The authorization code.
         * @return This builder instance.
         */
        public Builder code(String code) {
            return addParam("code", code);
        }

        /**
         * Sets the Client Secret if you have enabled the "Client Secret" feature in the Kakao Console.
         * This is optional unless the feature is enabled.
         *
         * @param clientSecret The Client Secret string.
         * @return This builder instance.
         */
        public Builder clientSecret(String clientSecret) {
            return addParam("client_secret", clientSecret);
        }

        @Override
        protected Builder self() { return this; }

        /**
         * Validates the presence of required parameters and builds the request.
         *
         * @return A new {@link KakaoTokenRequest} instance.
         * @throws kr.higu.exceptions.OAuthValidationException If any required parameter is missing.
         */
        @Override
        public KakaoTokenRequest build() {
            validate("grant_type", "client_id", "redirect_uri", "code");
            return new KakaoTokenRequest(this);
        }
    }

    @Override
    protected String getMethod() { return "POST"; }

    @Override
    protected URI getUri() {
        return URI.create("https://kauth.kakao.com/oauth/token");
    }

    /**
     * Parses the error response from Kakao Auth Server (kauth).
     * Extracts 'error_code' and 'error_description' from the JSON body.
     *
     * @param errorBody The raw error JSON response.
     * @return An {@link ErrorDetail} containing the parsed error info.
     */
    @Override
    protected ErrorDetail parseError(String errorBody) {
        try {
            JsonObject json = JsonParser.parseString(errorBody).getAsJsonObject();

            // Kakao returns 'error' as a string and sometimes 'error_code' as a specialized code.
            String errorCode = json.has("error_code")
                    ? json.get("error_code").getAsString()
                    : (json.has("error") ? json.get("error").getAsString() : "UNKNOWN_KAUTH_ERROR");

            String message = json.has("error_description")
                    ? json.get("error_description").getAsString()
                    : "No error description provided.";

            return new ErrorDetail(errorCode, message);
        } catch (Exception e) {
            return new ErrorDetail("PARSING_ERROR", "Failed to parse kauth error: " + errorBody);
        }
    }
}
