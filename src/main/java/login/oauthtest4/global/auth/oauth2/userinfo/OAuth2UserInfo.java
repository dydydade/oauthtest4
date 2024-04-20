package login.oauthtest4.global.auth.oauth2.userinfo;

import java.net.URL;
import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getSocialId(); //소셜 식별 값 : 구글 - "sub", 페이스북 - "id", 카카오 - "id", 네이버 - "id"

    public abstract String getNickname();

    public abstract URL getImageUrl();

    public abstract String getEmail();
}
