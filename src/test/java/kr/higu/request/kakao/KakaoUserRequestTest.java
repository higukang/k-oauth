package kr.higu.request.kakao;

import kr.higu.IHttpManager;
import kr.higu.dto.kakao.KakaoUserResponse;
import kr.higu.exceptions.OAuthValidationException;
import kr.higu.exceptions.detailed.OAuthResponseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class KakaoUserRequestTest {

    @Mock
    private IHttpManager httpManager;

    @Test
    @DisplayName("카카오 사용자 정보 요청 성공")
    void execute_Success() throws Exception {
        // given
        String successJson = """
                {
                     "id": 12345678,
                     "connected_at": "2026-01-29T08:56:41Z",
                     "properties": {
                         "nickname": "강희구",
                         "profile_image": "http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg",
                         "thumbnail_image": "http://img1.kakaocdn.net/thumb/R110x110.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg"
                     },
                     "kakao_account": {
                         "profile_nickname_needs_agreement": false,
                         "profile_image_needs_agreement": false,
                         "profile": {
                             "nickname": "강희구",
                             "thumbnail_image_url": "http://img1.kakaocdn.net/thumb/R110x110.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg",
                             "profile_image_url": "http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg",
                             "is_default_image": true,
                             "is_default_nickname": false
                         },
                         "name_needs_agreement": false,
                         "name": "강희구",
                         "has_email": true,
                         "email_needs_agreement": false,
                         "is_email_valid": true,
                         "is_email_verified": true,
                         "email": "higu@example.com",
                         "has_age_range": true,
                         "age_range_needs_agreement": false,
                         "age_range": "20~29",
                         "has_gender": true,
                         "gender_needs_agreement": false,
                         "gender": "male"
                     }
                 }
                """;
        given(httpManager.get(any(URI.class), any())).willReturn(successJson);

        KakaoUserRequest request = new KakaoUserRequest.Builder(httpManager)
                .accessToken("VALID_TOKEN")
                .secureResource(false)
                .propertyKeys()
                .build();

        // when
        KakaoUserResponse response = request.execute();

        // then
        assertThat(response.id()).isEqualTo("12345678");
        assertThat(response.kakaoAccount().email()).isEqualTo("higu@example.com");
    }

    @Test
    @DisplayName("유저 정보 조회 실패 - 액세스 토큰 누락, ValidationException 발생")
    void build_Error_When_AccessToken_Is_Missing() {
        // given
        KakaoUserRequest.Builder builder = new KakaoUserRequest.Builder(httpManager)
                .secureResource(true);

        // when, then
        assertThatThrownBy(builder::build)
                .isInstanceOf(OAuthValidationException.class)
                .hasMessageContaining("access token is required");
    }

    @Test
    @DisplayName("카카오 API 서버(kapi) 요청 에러 발생")
    void execute_Kapi_Error() throws Exception {
        // given
        int statusCode = 401;
        String errorJson = """
                {
                    "msg": "no authentication key!",
                    "code": -401
                }
                """;

        given(httpManager.get(any(URI.class), any()))
                .willThrow(new OAuthResponseException(statusCode, null, errorJson, "Unauthorized"));

        KakaoUserRequest request = new KakaoUserRequest.Builder(httpManager)
                .accessToken("EXPIRED_TOKEN")
                .build();

        // when, then
        assertThatThrownBy(request::execute)
                .isInstanceOf(OAuthResponseException.class)
                .satisfies(e -> {
                    OAuthResponseException ex = (OAuthResponseException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(401);
                    assertThat(ex.getErrorCode()).isEqualTo("-401");
                    assertThat(ex.getMessage()).contains("no authentication key!");
                });
    }
}