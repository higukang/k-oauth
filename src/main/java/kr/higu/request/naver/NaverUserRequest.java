package kr.higu.request.naver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.higu.IHttpManager;
import kr.higu.dto.naver.NaverUserResponse;
import kr.higu.exceptions.OAuthException;
import kr.higu.exceptions.OAuthValidationException;
import kr.higu.request.AbstractRequest;
import kr.higu.request.ErrorDetail;

import java.net.URI;

/**
 * Request class for retrieving the profile information of an authenticated Naver user.
 * It accesses the Naver Open API server using a Bearer Access Token.
 *
 * @see <a href="https://developers.naver.com/docs/login/profile/profile.md">Naver Profile API Documentation</a>
 * @author higukang
 */
public class NaverUserRequest extends AbstractRequest<NaverUserResponse> {

    private NaverUserRequest(Builder builder) {
        super(builder);
    }

    /**
     * Builder for creating {@link NaverUserRequest} instances.
     */
    public static class Builder extends AbstractRequest.Builder<NaverUserResponse, Builder> {
        /**
         * Initializes the builder for Naver Profile API.
         *
         * @param httpManager The HTTP manager to use for the request.
         */
        public Builder(IHttpManager httpManager) {
            super(httpManager, NaverUserResponse.class);
            this.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        }

        /**
         * Sets the access token for authentication.
         * The token is automatically prefixed with "Bearer " and added to the Authorization header.
         *
         * @param accessToken The Naver access token.
         * @return This builder instance.
         */
        public Builder accessToken(String accessToken) {
            return setHeader("Authorization", "Bearer " + accessToken);
        }

        @Override
        protected Builder self() { return this; }

        /**
         * Validates the presence of the Bearer access token and builds the request.
         *
         * @return A new {@link NaverUserRequest} instance.
         * @throws OAuthValidationException If the Authorization header is missing or does not start with "Bearer ".
         */
        @Override
        public NaverUserRequest build() throws OAuthException {
            String authHeader = this.headers.get("Authorization");
            if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
                throw new OAuthValidationException("[kr-oauth] A valid access token is required to retrieve Naver user information.");
            }
            return new NaverUserRequest(this);
        }
    }

    @Override
    protected String getMethod() { return "GET"; }

    @Override
    protected URI getUri() {
        return URI.create("https://openapi.naver.com/v1/nid/me");
    }

    /**
     * Parses the error response from Naver API.
     * Naver APIs may return different field names for errors (e.g., 'resultcode' vs 'errorCode').
     * This method normalizes them into a single {@link ErrorDetail}.
     *
     * @param errorBody The raw JSON error response from Naver.
     * @return A normalized {@link ErrorDetail} object.
     */
    @Override
    protected ErrorDetail parseError(String errorBody) {
        try {
            JsonObject json = JsonParser.parseString(errorBody).getAsJsonObject();

            // 1. errorCode 또는 resultcode 중 있는 것을 선택
            String errorCode = "NAVER_API_ERROR";
            if (json.has("resultcode")) {
                errorCode = json.get("resultcode").getAsString();
            } else if (json.has("errorCode")) {
                errorCode = json.get("errorCode").getAsString();
            }

            // 2. message 또는 errorMessage 중 있는 것을 선택
            String message = "No error message provided";
            if (json.has("message")) {
                message = json.get("message").getAsString();
            } else if (json.has("errorMessage")) {
                message = json.get("errorMessage").getAsString();
            }

            return new ErrorDetail(errorCode, message);
        } catch (Exception e) {
            return new ErrorDetail("PARSING_ERROR", "Failed to parse Naver API error: " + errorBody);
        }
    }
}
