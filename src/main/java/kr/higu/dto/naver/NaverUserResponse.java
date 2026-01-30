package kr.higu.dto.naver;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for Naver User Profile API.
 * Encapsulates the actual user data within the 'response' field.
 *
 * @see <a href="https://developers.naver.com/docs/login/profile/profile.md">Naver User Profile Documentation</a>
 */
public record NaverUserResponse(
        @SerializedName("resultcode") String resultCode,
        @SerializedName("message") String message,
        @SerializedName("response") Response response
) {
    public record Response(
            @SerializedName("id") String id,
            @SerializedName("nickname") String nickname,
            @SerializedName("name") String name,
            @SerializedName("email") String email,
            @SerializedName("gender") String gender,
            @SerializedName("age") String age,
            @SerializedName("birthday") String birthday,
            @SerializedName("profile_image") String profileImage,
            @SerializedName("birthyear") String birthyear,
            @SerializedName("mobile" ) String mobile
    ) {}
}
