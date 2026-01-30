package kr.higu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kr.higu.client.KakaoClient;
import kr.higu.client.NaverClient;
import kr.higu.dto.kakao.KakaoUserResponse;
import kr.higu.exceptions.OAuthException;
import kr.higu.exceptions.detailed.OAuthResponseException;
import kr.higu.request.kakao.KakaoPropertyKey;


public class Main {
    public static void main(String[] args) {
        KakaoClient kakaoClient = KakaoClient.create();
        NaverClient naverClient = NaverClient.create();

        try {
            KakaoUserResponse execute = kakaoClient.getUserInfo()
                    .accessToken("-r7rVN6OeNO360_rl0Gqo2x09hq9rov1AAAAAQoXEpYAAAGcDYP-JpgXPJRhmZ-F")
                    .secureResource(false)
                    .propertyKeys()
                    .build()
                    .execute();
            System.out.println(new Gson().toJson(execute));
        } catch (OAuthException e) {
            e.printStackTrace();
        }
//
//        System.out.println("----- Kakao Token Request ----");
//        try {
//            KakaoTokenResponse response = kakaoApi.getToken()
//                    .clientId("126983e9cb68d8c32fcec2de9d8c43a2")
//                    .redirectUri("http://localhost:5173")
//                    .code("rXW89ieITRGoVKGhjJiXP2vAvdh-25aY32qA4sTQgtP7ZGMLzc9itQAAAAQKFxZiAAABm_7R-g5b9Pmr5eg_ZA")
//                    .build()
//                    .execute();
//
//            System.out.println("token_type: " + response.tokenType());
//            System.out.println("access_token: " +response.accessToken());
//            System.out.println("id_token: " + response.idToken());
//            System.out.println("expires_in: " + response.expiresIn());
//            System.out.println("refresh_token: " + response.refreshToken());
//            System.out.println("refresh_token_expires_in: " + response.refreshTokenExpiresIn());
//            System.out.println("scope: " + response.scope());
//
//        } catch ( OAuthException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("----- Naver Token Request ----");
//        try {
//            NaverTokenResponse response = naverApi.getToken()
//                    .clientId("TGrFfZak2Str8pxMNDs8")
//                    .clientSecret("4oUwRUu7TV")
//                    .code("iYkJfYrX3uNX9nkOYU")
//                    .state("higukang")
//                    .build()
//                    .execute();
//
//            System.out.println("access_token: " + response.accessToken());
//            System.out.println("refresh_token: " + response.refreshToken());
//            System.out.println("token_type: " + response.tokenType());
//            System.out.println("expires_in: " + response.expiresIn());
//            System.out.println("error: " + response.error());
//            System.out.println("error_description: " + response.errorDescription());
//            String accessToken = response.accessToken();
//
//        } catch ( OAuthException e){
//            e.printStackTrace();
//        }

//        System.out.println("----- Naver User Request ----");
//        try {
//            NaverUserResponse execute = naverApi.getUserInfo()
//                    .accessToken("")
//                    .build()
//                    .execute();
//
//            System.out.println(execute.resultCode());
//            System.out.println(execute.message());
//            System.out.println(execute.response().id());
//            System.out.println(execute.response().nickname());
//            System.out.println(execute.response().name());
//            System.out.println(execute.response().email());
//            System.out.println(execute.response().gender());
//            System.out.println(execute.response().age());
//            System.out.println(execute.response().birthday());
//            System.out.println(execute.response().profileImage());
//            System.out.println(execute.response().birthyear());
//            System.out.println(execute.response().mobile());
//        } catch ( OAuthException e) {
//            e.printStackTrace();
//        }
//        // 1. 테스트: 카카오 토큰 요청 에러 (kauth - invalid code)
//        System.out.println("----- [Test 1] Kakao Token Error Parsing (kauth) ----");
//        try {
//            kakaoClient.getToken()
//                    .clientId("INVALID_CLIENT_ID") // 의도적 에러 유발
//                    .redirectUri("http://localhost:5173")
//                    .code("INVALID_CODE")
//                    .build()
//                    .execute();
//        } catch (OAuthResponseException e) {
//            e.printStackTrace();
//            printError(e); // 우리가 설계한 상세 에러 출력
//        } catch (OAuthException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("\n----- [Test 2] Kakao User Info Error Parsing (kapi) ----");
//
//        // 2. 테스트: 카카오 사용자 정보 요청 에러 (kapi - invalid token)
//        try {
//            kakaoClient.getUserInfo()
//                    .accessToken("INVALID_ACCESS_TOKEN") // 의도적 에러 유발
//                    .build()
//                    .execute();
//        } catch (OAuthResponseException e) {
//            e.printStackTrace();
//            printError(e);
//        } catch (OAuthException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("\n----- [Test 3] Naver Token Error Parsing (nid) ----");
//        // 3. 테스트: 네이버 토큰 요청 에러 (nid - invalid client)
//        try {
//            naverClient.getToken()
//                    .clientId("INVALID_NAVER_CLIENT_ID")
//                    .clientSecret("INVALID_SECRET")
//                    .code("INVALID_CODE")
//                    .state("higu_test")
//                    .build()
//                    .execute();
//        } catch (OAuthResponseException e) {
//            e.printStackTrace();
//            printError(e);
//        } catch ( OAuthException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("\n----- [Test 4] Naver User Info Error Parsing (openapi) ----");
//        // 4. 테스트: 네이버 사용자 정보 요청 에러 (openapi - invalid token)
//        try {
//            naverClient.getUserInfo()
//                    .accessToken("INVALID_NAVER_ACCESS_TOKEN")
//                    .build()
//                    .execute();
//        } catch (OAuthResponseException e) {
//            e.printStackTrace();
//            printError(e);
//        } catch ( OAuthException e) {
//            e.printStackTrace();
//        }
    }
        /**
         * Helper method to print detailed error information from OAuthResponseException.
         */
//        private static void printError(OAuthResponseException e){
//            System.out.println("Status Code : " + e.getStatusCode());
//            System.out.println("Error Code  : " + e.getErrorCode());
//            System.out.println("Message     : " + e.getMessage());
//            System.out.println("Raw Body    : " + e.getRawBody());
//        }
//    }
}