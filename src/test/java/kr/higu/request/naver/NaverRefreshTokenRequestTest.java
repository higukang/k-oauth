package kr.higu.request.naver;

import kr.higu.IHttpManager;
import kr.higu.dto.naver.NaverRefreshTokenResponse;
import kr.higu.exceptions.OAuthValidationException;
import kr.higu.exceptions.detailed.OAuthParsingException;
import kr.higu.exceptions.detailed.OAuthResponseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NaverRefreshTokenRequestTest {

    @Mock
    private IHttpManager httpManager;

    @Test
    @DisplayName("네이버 리프레시 토큰 갱신 성공")
    void execute_Success() throws Exception {
        // given
        String successJson = """
                {
                    "access_token": "NEW_ACCESS_TOKEN",
                    "token_type": "bearer",
                    "expires_in": "3600"
                }
                """;
        given(httpManager.post(any(URI.class), any(), any())).willReturn(successJson);

        NaverRefreshTokenRequest request = new NaverRefreshTokenRequest.Builder(httpManager)
                .clientId("CLIENT_ID")
                .clientSecret("SECRET")
                .refreshToken("REFRESH_TOKEN")
                .build();

        // when
        NaverRefreshTokenResponse response = request.execute();

        // then
        assertThat(response.accessToken()).isEqualTo("NEW_ACCESS_TOKEN");
        assertThat(response.tokenType()).isEqualTo("bearer");
        assertThat(response.expiresIn()).isEqualTo("3600");
        assertThat(response.error()).isNull();
        assertThat(response.errorDescription()).isNull();
    }

    @Test
    @DisplayName("네이버 리프레시 토큰 갱신 - 요청 바디에 refresh grant 파라미터 포함")
    void execute_RequestBody_Contains_RefreshGrantParams() throws Exception {
        // given
        given(httpManager.post(any(URI.class), any(), any())).willReturn("""
                {
                    "access_token": "ACCESS",
                    "token_type": "bearer",
                    "expires_in": "3600"
                }
                """);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        NaverRefreshTokenRequest request = new NaverRefreshTokenRequest.Builder(httpManager)
                .clientId("CLIENT_ID")
                .clientSecret("SECRET")
                .refreshToken("REFRESH_TOKEN")
                .build();

        // when
        request.execute();

        // then
        org.mockito.Mockito.verify(httpManager).post(any(URI.class), any(), bodyCaptor.capture());
        assertThat(bodyCaptor.getValue()).contains("grant_type=refresh_token");
        assertThat(bodyCaptor.getValue()).contains("client_id=CLIENT_ID");
        assertThat(bodyCaptor.getValue()).contains("client_secret=SECRET");
        assertThat(bodyCaptor.getValue()).contains("refresh_token=REFRESH_TOKEN");
        assertThat(bodyCaptor.getValue()).doesNotContain("code=");
        assertThat(bodyCaptor.getValue()).doesNotContain("state=");
    }

    @Test
    @DisplayName("필수 파라미터(client_id) 누락 시 빌드 에러")
    void build_Error_When_ClientId_Is_Missing() {
        // given
        NaverRefreshTokenRequest.Builder builder = new NaverRefreshTokenRequest.Builder(httpManager)
                .clientSecret("SECRET")
                .refreshToken("REFRESH_TOKEN");

        // when, then
        assertThatThrownBy(builder::build)
                .isInstanceOf(OAuthValidationException.class)
                .hasMessageContaining("client_id");
    }

    @Test
    @DisplayName("필수 파라미터(client_secret) 누락 시 빌드 에러")
    void build_Error_When_ClientSecret_Is_Missing() {
        // given
        NaverRefreshTokenRequest.Builder builder = new NaverRefreshTokenRequest.Builder(httpManager)
                .clientId("CLIENT_ID")
                .refreshToken("REFRESH_TOKEN");

        // when, then
        assertThatThrownBy(builder::build)
                .isInstanceOf(OAuthValidationException.class)
                .hasMessageContaining("client_secret");
    }

    @Test
    @DisplayName("필수 파라미터(refresh_token) 누락 시 빌드 에러")
    void build_Error_When_RefreshToken_Is_Missing() {
        // given
        NaverRefreshTokenRequest.Builder builder = new NaverRefreshTokenRequest.Builder(httpManager)
                .clientId("CLIENT_ID")
                .clientSecret("SECRET");

        // when, then
        assertThatThrownBy(builder::build)
                .isInstanceOf(OAuthValidationException.class)
                .hasMessageContaining("refresh_token");
    }

    @Test
    @DisplayName("200 OK인데 바디에 에러가 있는 경우")
    void execute_Error_With_200_Ok() throws Exception {
        // given
        String errorJson = """
                {
                    "error": "invalid_request",
                    "error_description": "invalid refresh token"
                }
                """;
        given(httpManager.post(any(URI.class), any(), any())).willReturn(errorJson);

        NaverRefreshTokenRequest request = new NaverRefreshTokenRequest.Builder(httpManager)
                .clientId("CLIENT_ID")
                .clientSecret("SECRET")
                .refreshToken("REFRESH_TOKEN")
                .build();

        // when, then
        assertThatThrownBy(request::execute)
                .isInstanceOf(OAuthResponseException.class)
                .satisfies(e -> {
                    OAuthResponseException ex = (OAuthResponseException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(200);
                    assertThat(ex.getErrorCode()).isEqualTo("invalid_request");
                    assertThat(ex.getMessage()).contains("invalid refresh token");
                });
    }

    @Test
    @DisplayName("200 OK인데 malformed JSON이면 OAuthParsingException")
    void execute_Error_With_200_Ok_MalformedJson() throws Exception {
        // given
        given(httpManager.post(any(URI.class), any(), any())).willReturn("not-json");

        NaverRefreshTokenRequest request = new NaverRefreshTokenRequest.Builder(httpManager)
                .clientId("CLIENT_ID")
                .clientSecret("SECRET")
                .refreshToken("REFRESH_TOKEN")
                .build();

        // when, then
        assertThatThrownBy(request::execute)
                .isInstanceOf(OAuthParsingException.class)
                .hasMessageContaining("Failed to parse NaverRefreshTokenResponse response");
    }
}
