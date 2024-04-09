package login.oauthtest4.global.auth.oauth2.service;

import login.oauthtest4.domain.user.SocialProfile;
import login.oauthtest4.domain.user.SocialType;
import login.oauthtest4.domain.user.User;
import login.oauthtest4.domain.user.exception.RegisteredUserNotFoundException;
import login.oauthtest4.domain.user.repository.SocialProfileRepository;
import login.oauthtest4.domain.user.repository.UserRepository;
import login.oauthtest4.global.auth.oauth2.OAuthAttributes;
import login.oauthtest4.global.auth.oauth2.CustomOAuth2User;
import login.oauthtest4.global.auth.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final SocialProfileRepository socialProfileRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";
    private static final String FACEBOOK = "facebook";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        /**
         * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
         * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
         * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
         * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /**
         * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
         * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
         * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attributes = oAuth2User.getAttributes(); // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        User createdUser = getUser(extractAttributes, socialType); // getUser() 메소드로 User 객체 생성 후 반환

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser.getEmail(),
                createdUser.getRole()
        );
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

    /**
     * SocialType과 attributes에 들어있는 소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메소드
     * 만약 찾은 회원이 있다면, 그대로 반환하고 없다면 saveUser()를 호출하여 회원을 저장한다.
     */
    private User getUser(OAuthAttributes attributes, SocialType socialType) {

        // 리소스 서버로부터 넘겨받은 user 정보
        OAuth2UserInfo oauth2UserInfo = attributes.getOauth2UserInfo();

        // 리소스 서버로부터 넘겨받은 socialEmail
        String socialEmail = oauth2UserInfo.getEmail();

        // 리소스 서버로부터 넘겨받은 socialId
        String socialId = oauth2UserInfo.getSocialId();

        // 리소스 서버에서 넘겨받은 socialEmail 과 앱 계정의 email 이 일치하는 사용자 조회
        // 사용자가 존재하지 않으면, RegisteredUserNotFoundException 던짐
        User user = userRepository.findByEmail(socialEmail)
                .orElseThrow(RegisteredUserNotFoundException::new); // 회원가입 페이지로 보냄

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

        // 소셜 로그인 시도한 socialProfile 이 이미 연동되어있는 경우,
        // 인증 성공시키고 홈 화면으로 보냄
        return user;
    }

    /**
     * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
     * 생성된 User 객체를 DB에 저장 : socialType, socialId, email, role 값만 있는 상태
     */
    private User saveUser(OAuthAttributes attributes, SocialType socialType) {
        User createdUser = attributes.toEntity(socialType, attributes.getOauth2UserInfo());
        return userRepository.save(createdUser);
    }
}
