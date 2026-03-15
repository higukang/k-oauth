package kr.higu;

import com.google.gson.Gson;
import kr.higu.client.KakaoClient;
import kr.higu.client.NaverClient;
import kr.higu.dto.kakao.KakaoUserResponse;
import kr.higu.exceptions.OAuthException;

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
    }
}