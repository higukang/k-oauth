package kr.higu.request.naver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.higu.IHttpManager;
import kr.higu.dto.naver.NaverRefreshTokenResponse;
import kr.higu.exceptions.OAuthException;
import kr.higu.exceptions.detailed.OAuthParsingException;
import kr.higu.exceptions.detailed.OAuthResponseException;
import kr.higu.request.AbstractRequest;
import kr.higu.request.ErrorDetail;

import java.net.URI;

/**
 * Request class for refreshing a Naver access token with a refresh token.
 * This class follows the refresh token flow in the Naver Login API.
 *
 * @see <a href="https://developers.naver.com/docs/login/api/api.md">Naver Token API Documentation</a>
 * @author higukang
 */
public class NaverRefreshTokenRequest extends AbstractRequest<NaverRefreshTokenResponse> {

    private NaverRefreshTokenRequest(Builder builder) {
        super(builder);
    }

    /**
     * Builder for creating {@link NaverRefreshTokenRequest} instances.
     * Requires clientId, clientSecret, and refreshToken to be set.
     */
    public static class Builder extends AbstractRequest.Builder<NaverRefreshTokenResponse, Builder> {

        /**
         * Initializes the builder with Naver-specific default headers and refresh grant type.
         *
         * @param httpManager The HTTP manager to use for the request.
         */
        public Builder(IHttpManager httpManager) {
            super(httpManager, NaverRefreshTokenResponse.class);
            this.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            this.addParam("grant_type", "refresh_token");
        }

        /**
         * Sets the Client ID issued when registering the application on Naver Developers.
         *
         * @param clientId The Naver Client ID.
         * @return This builder instance.
         */
        public Builder clientId(String clientId) {
            return addParam("client_id", clientId);
        }

        /**
         * Sets the Client Secret issued when registering the application on Naver Developers.
         *
         * @param clientSecret The Naver Client Secret.
         * @return This builder instance.
         */
        public Builder clientSecret(String clientSecret) {
            return addParam("client_secret", clientSecret);
        }

        /**
         * Sets the refresh token previously issued by Naver.
         *
         * @param refreshToken The refresh token.
         * @return This builder instance.
         */
        public Builder refreshToken(String refreshToken) {
            return addParam("refresh_token", refreshToken);
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Validates mandatory parameters and builds the {@link NaverRefreshTokenRequest}.
         *
         * @return A new request instance.
         * @throws kr.higu.exceptions.OAuthValidationException If any required parameter is missing.
         */
        @Override
        public NaverRefreshTokenRequest build() {
            validate("grant_type", "client_id", "client_secret", "refresh_token");
            return new NaverRefreshTokenRequest(this);
        }
    }

    @Override
    protected String getMethod() { return "POST"; }

    @Override
    protected URI getUri() {
        return URI.create("https://nid.naver.com/oauth2.0/token");
    }

    /**
     * Parses error responses from the Naver Auth Server.
     * Naver uses 'error' and 'error_description' fields in its JSON error body.
     *
     * @param errorBody The raw JSON error response.
     * @return A parsed {@link ErrorDetail}.
     */
    @Override
    protected ErrorDetail parseError(String errorBody) {
        try {
            JsonObject json = JsonParser.parseString(errorBody).getAsJsonObject();
            String errorCode = json.has("error") ? json.get("error").getAsString() : "NAVER_AUTH_ERROR";
            String message = json.has("error_description") ? json.get("error_description").getAsString() : "No description provided";
            return new ErrorDetail(errorCode, message);
        } catch (Exception e) {
            return new ErrorDetail("PARSING_ERROR", "Failed to parse Naver auth error: " + errorBody);
        }
    }

    /**
     * Handles the Naver case where the server returns a 200 OK status
     * but the response body contains an 'error' field.
     *
     * @param responseBody The raw response body from Naver.
     * @throws OAuthException If the body contains an 'error' field or the response cannot be parsed.
     */
    @Override
    protected void validateSuccessResponse(String responseBody) throws OAuthException {
        final JsonObject json;
        try {
            json = JsonParser.parseString(responseBody).getAsJsonObject();
        } catch (Exception e) {
            throw new OAuthParsingException(
                    "[K-OAuth] Failed to parse NaverRefreshTokenResponse response: " + e.getMessage(),
                    e
            );
        }

        if (json.has("error")) {
            ErrorDetail detail = parseError(responseBody);
            throw new OAuthResponseException(200, detail.errorCode(), responseBody, detail.message());
        }
    }
}
