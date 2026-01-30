package kr.higu.request.kakao;

/**
 * Enumeration of property keys for selective user information retrieval from Kakao.
 * These keys correspond to the dot-notation paths in the Kakao user response.
 *
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#propertykeys">Kakao Property Keys Documentation</a>
 * @author higukang
 */
public enum KakaoPropertyKey {
    /** The user's profile information (nickname, profile image). */
    PROFILE("kakao_account.profile"),
    /** The user's real name. */
    NAME("kakao_account.name"),
    /** The user's email address. */
    EMAIL("kakao_account.email"),
    /** The user's age range. */
    AGE_RANGE("kakao_account.age_range"),
    /** The user's birthday. */
    BIRTHDAY("kakao_account.birthday"),
    /** The user's gender. */
    GENDER("kakao_account.gender");

    private final String key;

    KakaoPropertyKey(String key) {
        this.key = key;
    }

    /** @return The string key required by Kakao API. */
    public String getKey() {
        return key;
    }
}
