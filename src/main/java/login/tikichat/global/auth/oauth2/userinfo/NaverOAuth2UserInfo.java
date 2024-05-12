package login.tikichat.global.auth.oauth2.userinfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getSocialId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }
        return (String) response.get("id");
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getNickname() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("nickname");
    }

    @Override
    @SuppressWarnings("unchecked")
    public URL getImageUrl() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        try {
            // `attributes.get("picture")`에서 Object를 String으로 캐스팅
            String urlString = (String) response.get("profile_image");
            // String을 URL 객체로 변환
            return new URL(urlString);
        } catch (ClassCastException | MalformedURLException e) {
            // 적절한 예외 처리: 캐스팅 오류 또는 URL 형식 오류 발생 시
            e.printStackTrace();
            return null; // 또는 적절한 예외를 던지거나 다른 URL을 반환할 수 있음
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("email");
    }
}
