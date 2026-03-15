package kr.higu.request.kakao;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.higu.IHttpManager;
import kr.higu.dto.kakao.KakaoRefreshTokenResponse;
import kr.higu.request.AbstractRequest;
import kr.higu.request.ErrorDetail;

import java.net.URI;

/**
 * Request class for refreshing a Kakao access token using a refresh token.
 * This class follows the "Refresh token" section of the Kakao Login REST API.
 *
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#refresh-token">Kakao Refresh Token Documentation</a>
 * @author higukang
 */
public class KakaoRefreshTokenRequest extends AbstractRequest<KakaoRefreshTokenResponse> {

    private KakaoRefreshTokenRequest(Builder builder) {
        super(builder);
    }

    /**
     * Builder for creating {@link KakaoRefreshTokenRequest} instances.
     */
    public static class Builder extends AbstractRequest.Builder<KakaoRefreshTokenResponse, Builder> {

        /**
         * Initializes the builder with Kakao-specific default headers and the mandatory refresh grant type.
         *
         * @param httpManager The HTTP manager to use for the request.
         */
        public Builder(IHttpManager httpManager) {
            super(httpManager, KakaoRefreshTokenResponse.class);
            this.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            this.addParam("grant_type", "refresh_token");
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
         * Sets the refresh token previously issued by Kakao.
         *
         * @param refreshToken The refresh token.
         * @return This builder instance.
         */
        public Builder refreshToken(String refreshToken) {
            return addParam("refresh_token", refreshToken);
        }

        /**
         * Sets the Client Secret if you have enabled the "Client Secret" feature in the Kakao Console.
         * This is optional unless the feature is enabled.
         * A {@code null} value is ignored.
         *
         * @param clientSecret The Client Secret string.
         * @return This builder instance.
         */
        public Builder clientSecret(String clientSecret) {
            return addParam("client_secret", clientSecret);
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Validates the presence of required parameters and builds the request.
         *
         * @return A new {@link KakaoRefreshTokenRequest} instance.
         * @throws kr.higu.exceptions.OAuthValidationException If any required parameter is missing.
         */
        @Override
        public KakaoRefreshTokenRequest build() {
            validate("grant_type", "client_id", "refresh_token");
            return new KakaoRefreshTokenRequest(this);
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
