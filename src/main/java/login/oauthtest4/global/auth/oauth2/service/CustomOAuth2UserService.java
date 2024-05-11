package login.oauthtest4.global.auth.oauth2.service;

import login.oauthtest4.domain.user.model.SocialProfile;
import login.oauthtest4.domain.user.model.SocialType;
import login.oauthtest4.domain.user.model.User;
import login.oauthtest4.domain.user.repository.SocialProfileRepository;
import login.oauthtest4.domain.user.repository.UserRepository;
import login.oauthtest4.global.auth.oauth2.OAuthAttributes;
import login.oauthtest4.global.auth.oauth2.dto.OAuth2UserDto;
import login.oauthtest4.global.auth.oauth2.dto.UserDto;
import login.oauthtest4.global.auth.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService {

    private final UserRepository userRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";
    private static final String FACEBOOK = "facebook";


    public OAuth2UserDto socialLogin(String registrationId, String oauth2AccessTokenStr) {
        // 리소스 서버에 유저 정보를 요청하기 위한 객체 생성
        OAuth2UserRequest userRequest = createUserRequest(registrationId, oauth2AccessTokenStr);

        // 리소스 서버로부터 OAuth2User 객체 받아옴
        OAuth2User oAuth2User = loadOAuth2User(userRequest);

        // 받아온 OAuth2User 에서 사용자 정보(OAuthAttributes) 추출
        OAuthAttributes oAuthAttributes = extractUserAttributes(userRequest, oAuth2User);

        // 소셜 로그인, 회원가입 처리 후 OAuth2UserDto 객체 반환
        return loadUser(registrationId, oAuthAttributes);
    }

    private OAuth2UserDto loadUser(String registrationId, OAuthAttributes oAuthAttributes) throws OAuth2AuthenticationException {
        // 리소스 서버로부터 넘겨받은 user 정보
        OAuth2UserInfo oauth2UserInfo = oAuthAttributes.getOauth2UserInfo();

        // 리소스 서버로부터 넘겨받은 socialEmail
        String socialEmail = oauth2UserInfo.getEmail();

        // 리소스 서버로부터 넘겨받은 socialId
        String socialId = oauth2UserInfo.getSocialId();
        SocialType socialType = getSocialType(registrationId);

        // 리소스 서버에서 넘겨받은 socialEmail 과 앱 계정의 email 이 일치하는 사용자 조회
        User user = userRepository.findByEmail(socialEmail).orElse(null);

        Map<String, Object> socialData = new HashMap<>();
        socialData.put("socialEmail", socialEmail);
        socialData.put("socialId", socialId);
        socialData.put("socialType", socialType);

        if (user == null) {
            return new OAuth2UserDto(false, null, socialData);
        }

        // 일반 계정으로 가입한 적이 있는 이메일인지 검증
        if (user.isPasswordExistUser()) {
            checkAndLinkSocialProfile(user, socialEmail, socialId, socialType);
        }

        // 찾은 유저 정보 반환(프론트에서 인증 성공시키고 홈 화면으로 이동하도록)
        return new OAuth2UserDto(true, new UserDto(user.getId(), user.getEmail(), user.getNickname(), user.getImageUrl()), socialData);
    }

    /**
     * 클라이언트에서 넘겨받은 registrationId 와 oauth2AccessTokenStr 정보로 userRequest 를 생성하는 메서드
     * @param registrationId
     * @param oauth2AccessTokenStr
     * @return OAuth2UserRequest: 리소스 서버에 사용자 정보를 요청하기 위한 객체
     */
    private OAuth2UserRequest createUserRequest(String registrationId, String oauth2AccessTokenStr) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                oauth2AccessTokenStr,
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.HOURS)
        );

        OAuth2UserRequest userRequest = new OAuth2UserRequest(
                clientRegistration,
                oAuth2AccessToken
        );
        return userRequest;
    }

    /**
     * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
     * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
     * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
     * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
     */
    private OAuth2User loadOAuth2User(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        return oAuth2User;
    }

    /**
     * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
     * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
     * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
     */
    private OAuthAttributes extractUserAttributes(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attributes = oAuth2User.getAttributes(); // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);
        return extractAttributes;
    }

    /**
     * 사용자 계정이 소셜 연동이 되어있는지 체크하고 소셜 연동
     * @param user
     * @param socialEmail
     * @param socialId
     * @param socialType
     */
    private void checkAndLinkSocialProfile(User user, String socialEmail, String socialId, SocialType socialType) {
        // app 계정이 존재하는 경우, 연동된 socialProfile 조회
        Optional<SocialProfile> socialProfileOptional = socialProfileRepository.findBySocialEmailAndSocialTypeWithUser(socialEmail, socialType);

        // socialProfile 연동이 안 되어있는 경우,
        if (socialProfileOptional.isEmpty()) {
            // socialProfile 자동 연동해준 뒤 홈 화면으로 이동시킴
            SocialProfile socialProfile = SocialProfile.builder()
                    .user(user)
                    .socialType(socialType)
                    .socialId(socialId)
                    .socialEmail(socialEmail)
                    .build();
            socialProfileRepository.save(socialProfile); // 소셜 프로필 연동
        }
    }

    private SocialType getSocialType(String registrationId) {
        if(NAVER.equals(registrationId)) {
            return SocialType.NAVER;
        }
        if(KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        }
        if(FACEBOOK.equals(registrationId)) {
            return SocialType.FACEBOOK;
        }
        return SocialType.GOOGLE;
    }
}
