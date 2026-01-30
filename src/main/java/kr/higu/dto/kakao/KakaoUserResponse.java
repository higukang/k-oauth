package kr.higu.dto.kakao;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for Kakao User Information API.
 * Contains unique user ID, account details, and profile information.
 *
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info">Kakao User Info API Documentation</a>
 * @author higukang
 */
public record KakaoUserResponse(
        /** Unique identifier for the Kakao user. */
        @SerializedName("id") String id,
        /** Whether the user has already signed up for the service. */
        @SerializedName("has_signed_up") Boolean hasSignedUp,
        /** Time when the user connected to the app (UTC). */
        @SerializedName("connected_at") String connectedAt,
        /** Time when the user information was last synchronized. */
        @SerializedName("synched_at") String synchedAt,
        /** Custom properties provided by the app. */
        @SerializedName("properties") JsonObject properties,
        /** Detailed account information including email, gender, etc. */
        @SerializedName("kakao_account") KakaoAccount kakaoAccount,
        /** Partner-specific information. */
        @SerializedName("partner") Partner partner
) {
    /**
     * Represents the user's account information and privacy settings.
     */
    public record KakaoAccount(
            @SerializedName("profile_needs_agreement") Boolean profileNeedsAgreement,
            @SerializedName("profile_nickname_needs_agreement") Boolean profileNicknameNeedsAgreement,
            @SerializedName("profile_image_needs_agreement") Boolean profileImageNeedsAgreement,
            /** Basic profile information (nickname, profile image). */
            @SerializedName("profile") Profile profile,
            @SerializedName("name_needs_agreement") Boolean nameNeedsAgreement,
            @SerializedName("name") String name,
            @SerializedName("email_needs_agreement") Boolean emailNeedsAgreement,
            @SerializedName("is_email_valid") Boolean isEmailValid,
            @SerializedName("is_email_verified") Boolean isEmailVerified,
            @SerializedName("email") String email,
            @SerializedName("age_range_needs_agreement") Boolean ageRangeNeedsAgreement,
            @SerializedName("age_range") String ageRange,
            @SerializedName("birthyear_needs_agreement") Boolean birthyearNeedsAgreement,
            @SerializedName("birthyear") String birthyear,
            @SerializedName("birthday_needs_agreement") Boolean birthdayNeedsAgreement,
            @SerializedName("birthday") String birthday,
            /** Type of birthday: SOLAR or LUNAR. */
            @SerializedName("birthday_type") String birthdayType,
            @SerializedName("is_leap_month") Boolean isLeanMonth,
            @SerializedName("gender_needs_agreement") Boolean genderNeedsAgreement,
            @SerializedName("gender") String gender, //female, male
            @SerializedName("phone_number_needs_agreement") Boolean phoneNumberNeedsAgreement,
            @SerializedName("phone_number") String phoneNumber,
            @SerializedName("ci_needs_agreement") Boolean ciNeedsAgreement,
            @SerializedName("ci") String ci,
            /** Time when CI was authenticated (UTC). */
            @SerializedName("ci_authenticated_at") String ciAuthenticatedAt
    ) {
        /**
         * Represents the user's public profile data.
         */
        public record Profile(
                @SerializedName("nickname") String nickname,
                @SerializedName("thumbnail_image_url") String thumbnailImageUrl,
                @SerializedName("profile_image_url") String profileImageUrl,
                @SerializedName("is_default_image") Boolean isDefaultImage,
                @SerializedName("is_default_nickname") Boolean isDefaultNickname
        ) {
        }
    }
    /**
     * Partner-specific data such as UUID.
     */
    public record Partner(
            @SerializedName("uuid") String uuid
    ) {}
}
