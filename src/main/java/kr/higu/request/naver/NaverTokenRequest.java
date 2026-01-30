package kr.higu.request.naver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.higu.IHttpManager;
import kr.higu.dto.naver.NaverTokenResponse;
import kr.higu.exceptions.OAuthException;
import kr.higu.exceptions.detailed.OAuthResponseException;
import kr.higu.request.AbstractRequest;
import kr.higu.request.ErrorDetail;

import java.net.URI;

/**
 * Request class for exchanging an authorization code for a Naver access token.
 * This class handles the specific nuances of the Naver Auth Server,
 * including its unique error handling on 200 OK responses.
 *
 * @see <a href="https://developers.naver.com/docs/login/api/api.md">Naver Token API Documentation</a>
 * @author higukang
 */
public class NaverTokenRequest extends AbstractRequest<NaverTokenResponse> {

    private NaverTokenRequest(Builder builder) {
        super(builder);
    }

    /**
     * Builder for creating {@link NaverTokenRequest} instances.
     * Requires clientId, clientSecret, code, and state to be set.
     */
    public static class Builder extends AbstractRequest.Builder<NaverTokenResponse, Builder> {

        /**
         * Initializes the builder with Naver-specific default headers and grant_type.
         *
         * @param httpManager The HTTP manager to use for the request.
         */
        public Builder(IHttpManager httpManager) {
            super(httpManager, NaverTokenResponse.class);
            this.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            this.addParam("grant_type", "authorization_code");
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
         * Sets the authorization code received from the Naver login process.
         *
         * @param code The authorization code.
         * @return This builder instance.
         */
        public Builder code(String code) {
            return addParam("code", code);
        }

        /**
         * Sets the state value used to prevent CSRF attacks.
         * Must be the same value as sent in the initial authorization request.
         *
         * @param state A unique state string.
         * @return This builder instance.
         */
        public Builder state(String state) {
            return addParam("state", state);
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Validates mandatory parameters and builds the {@link NaverTokenRequest}.
         *
         * @return A new request instance.
         * @throws kr.higu.exceptions.OAuthValidationException If any required parameter is missing.
         */
        @Override
        public NaverTokenRequest build() {
            validate("grant_type", "client_id", "client_secret", "code", "state");
            return new NaverTokenRequest(this);
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
            // Naver Auth uses 'error' and 'error_description'
            String errorCode = json.has("error") ? json.get("error").getAsString() : "NAVER_AUTH_ERROR";
            String message = json.has("error_description") ? json.get("error_description").getAsString() : "No description provided";
            return new ErrorDetail(errorCode, message);
        } catch (Exception e) {
            return new ErrorDetail("PARSING_ERROR", "Failed to parse Naver auth error: " + errorBody);
        }
    }

    /**
     * Handles a specific case for Naver where the server returns a 200 OK status
     * but the response body contains an 'error' field.
     *
     * @param responseBody The raw response body from Naver.
     * @throws OAuthException If the body contains an 'error' field.
     */
    @Override
    protected void validateSuccessResponse(String responseBody) throws OAuthException {
        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

        // Check if 'error' field exists even if HTTP status is 200
        if (json.has("error")) {
            ErrorDetail detail = parseError(responseBody);
            throw new OAuthResponseException(200, detail.errorCode(), responseBody, detail.message());
        }
    }
}
