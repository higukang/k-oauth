package kr.higu.request.kakao;

import kr.higu.IHttpManager;
import kr.higu.dto.kakao.KakaoRefreshTokenResponse;
import kr.higu.exceptions.OAuthValidationException;
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
class KakaoRefreshTokenRequestTest {

    @Mock
    private IHttpManager httpManager;

    @Test
    @DisplayName("카카오 리프레시 토큰 갱신 성공")
    void execute_Success() throws Exception {
        // given
        String successJson = """
                {
                    "access_token": "NEW_ACCESS_TOKEN",
                    "token_type": "bearer",
                    "expires_in": 43199
                }
                """;
        given(httpManager.post(any(URI.class), any(), any())).willReturn(successJson);

        KakaoRefreshTokenRequest request = new KakaoRefreshTokenRequest.Builder(httpManager)
                .clientId("TEST_ID")
                .refreshToken("REFRESH_TOKEN")
                .clientSecret("TEST_SECRET")
                .build();

        // when
        KakaoRefreshTokenResponse response = request.execute();

        // then
        assertThat(response.accessToken()).isEqualTo("NEW_ACCESS_TOKEN");
        assertThat(response.tokenType()).isEqualTo("bearer");
        assertThat(response.expiresIn()).isEqualTo(43199);
        assertThat(response.idToken()).isNull();
        assertThat(response.refreshToken()).isNull();
        assertThat(response.refreshTokenExpiresIn()).isNull();
    }

    @Test
    @DisplayName("카카오 리프레시 토큰 갱신 - id_token 포함 응답 파싱")
    void execute_Success_With_IdToken() throws Exception {
        // given
        String successJson = """
                {
                    "access_token": "NEW_ACCESS_TOKEN",
                    "token_type": "bearer",
                    "id_token": "NEW_ID_TOKEN",
                    "expires_in": 43199
                }
                """;
        given(httpManager.post(any(URI.class), any(), any())).willReturn(successJson);

        KakaoRefreshTokenRequest request = new KakaoRefreshTokenRequest.Builder(httpManager)
                .clientId("TEST_ID")
                .refreshToken("REFRESH_TOKEN")
                .build();

        // when
        KakaoRefreshTokenResponse response = request.execute();

        // then
        assertThat(response.idToken()).isEqualTo("NEW_ID_TOKEN");
    }

    @Test
    @DisplayName("카카오 리프레시 토큰 갱신 - refresh_token 갱신 응답 파싱")
    void execute_Success_With_NewRefreshToken() throws Exception {
        // given
        String successJson = """
                {
                    "access_token": "NEW_ACCESS_TOKEN",
                    "token_type": "bearer",
                    "refresh_token": "NEW_REFRESH_TOKEN",
                    "refresh_token_expires_in": 5184000,
                    "expires_in": 43199
                }
                """;
        given(httpManager.post(any(URI.class), any(), any())).willReturn(successJson);

        KakaoRefreshTokenRequest request = new KakaoRefreshTokenRequest.Builder(httpManager)
                .clientId("TEST_ID")
                .refreshToken("REFRESH_TOKEN")
                .build();

        // when
        KakaoRefreshTokenResponse response = request.execute();

        // then
        assertThat(response.refreshToken()).isEqualTo("NEW_REFRESH_TOKEN");
        assertThat(response.refreshTokenExpiresIn()).isEqualTo(5184000);
    }

    @Test
    @DisplayName("카카오 리프레시 토큰 갱신 - 요청 바디에 refresh grant 파라미터 포함")
    void execute_RequestBody_Contains_RefreshGrantParams() throws Exception {
        // given
        given(httpManager.post(any(URI.class), any(), any())).willReturn("""
                {
                    "access_token": "ACCESS",
                    "token_type": "bearer",
                    "expires_in": 43199
                }
                """);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        KakaoRefreshTokenRequest request = new KakaoRefreshTokenRequest.Builder(httpManager)
                .clientId("TEST_ID")
                .refreshToken("REFRESH_TOKEN")
                .clientSecret("TEST_SECRET")
                .build();

        // when
        request.execute();

        // then
        org.mockito.Mockito.verify(httpManager).post(any(URI.class), any(), bodyCaptor.capture());
        assertThat(bodyCaptor.getValue()).contains("grant_type=refresh_token");
        assertThat(bodyCaptor.getValue()).contains("client_id=TEST_ID");
        assertThat(bodyCaptor.getValue()).contains("refresh_token=REFRESH_TOKEN");
        assertThat(bodyCaptor.getValue()).contains("client_secret=TEST_SECRET");
    }

    @Test
    @DisplayName("카카오 리프레시 토큰 갱신 - optional client_secret이 null이면 파라미터에서 제외")
    void execute_NullOptionalClientSecret_Ignored() throws Exception {
        // given
        given(httpManager.post(any(URI.class), any(), any())).willReturn("""
                {
                    "access_token": "ACCESS",
                    "token_type": "bearer",
                    "expires_in": 43199
                }
                """);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        KakaoRefreshTokenRequest request = new KakaoRefreshTokenRequest.Builder(httpManager)
                .clientId("TEST_ID")
                .refreshToken("REFRESH_TOKEN")
                .clientSecret(null)
                .build();

        // when
        request.execute();

        // then
        org.mockito.Mockito.verify(httpManager).post(any(URI.class), any(), bodyCaptor.capture());
        assertThat(bodyCaptor.getValue()).doesNotContain("client_secret");
    }

    @Test
    @DisplayName("카카오 리프레시 토큰 갱신 실패 - client_id 누락")
    void build_Error_When_ClientId_Is_Missing() {
        // given
        KakaoRefreshTokenRequest.Builder builder = new KakaoRefreshTokenRequest.Builder(httpManager)
                .refreshToken("REFRESH_TOKEN")
                .clientSecret("TEST_SECRET");

        // when, then
        assertThatThrownBy(builder::build)
                .isInstanceOf(OAuthValidationException.class)
                .hasMessageContaining("client_id");
    }

    @Test
    @DisplayName("카카오 리프레시 토큰 갱신 실패 - refresh_token 누락")
    void build_Error_When_RefreshToken_Is_Missing() {
        // given
        KakaoRefreshTokenRequest.Builder builder = new KakaoRefreshTokenRequest.Builder(httpManager)
                .clientId("TEST_ID")
                .clientSecret("TEST_SECRET");

        // when, then
        assertThatThrownBy(builder::build)
                .isInstanceOf(OAuthValidationException.class)
                .hasMessageContaining("refresh_token");
    }

    @Test
    @DisplayName("카카오 리프레시 토큰 갱신 실패 - 카카오 에러 응답 파싱")
    void execute_ErrorResponse() throws Exception {
        // given
        int statusCode = 400;
        String errorJson = """
                {
                    "error": "invalid_grant",
                    "error_description": "refresh token is invalid",
                    "error_code": "KOE320"
                }
                """;

        given(httpManager.post(any(URI.class), any(), any()))
                .willThrow(new OAuthResponseException(statusCode, null, errorJson, "Server Error"));

        KakaoRefreshTokenRequest request = new KakaoRefreshTokenRequest.Builder(httpManager)
                .clientId("TEST_ID")
                .refreshToken("INVALID_REFRESH_TOKEN")
                .build();

        // when, then
        assertThatThrownBy(request::execute)
                .isInstanceOf(OAuthResponseException.class)
                .satisfies(e -> {
                    OAuthResponseException ex = (OAuthResponseException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(400);
                    assertThat(ex.getErrorCode()).isEqualTo("KOE320");
                    assertThat(ex.getMessage()).contains("refresh token is invalid");
                });
    }
}
