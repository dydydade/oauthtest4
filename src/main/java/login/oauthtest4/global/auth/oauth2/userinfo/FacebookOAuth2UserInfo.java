package login.oauthtest4.global.auth.oauth2.userinfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {

    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getSocialId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("name");
    }

    @Override
    public URL getImageUrl() {
        try {
            // `attributes.get("picture")`에서 Object를 String으로 캐스팅
            String urlString = (String) attributes.get("picture");
            // String을 URL 객체로 변환
            return new URL(urlString);
        } catch (ClassCastException | MalformedURLException e) {
            // 적절한 예외 처리: 캐스팅 오류 또는 URL 형식 오류 발생 시
            e.printStackTrace();
            return null; // 또는 적절한 예외를 던지거나 다른 URL을 반환할 수 있음
        }
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
