package login.oauthtest4.global.config;

import login.oauthtest4.domain.user.model.Role;
import login.oauthtest4.domain.user.model.SocialProfile;
import login.oauthtest4.domain.user.model.SocialType;
import login.oauthtest4.domain.user.model.User;
import login.oauthtest4.domain.user.repository.SocialProfileRepository;
import login.oauthtest4.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 초기 상태 등록 Config
 */
@Configuration
@RequiredArgsConstructor
@Profile(value = "!test") // test 에서는 제외
public class InitializeDefaultConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 앱 계정(User) 및 소셜 연동 정보 저장
     */
    @Override
    public void run(String... args) throws Exception {
        User user = User.builder()
                .id(1L)
                .username("dydydade")
                .email("dydydade@gmail.com")
                .role(Role.USER)
                .password(passwordEncoder.encode("1234"))
                .build();
        SocialProfile naver = SocialProfile.builder()
                .id(1L)
                .socialEmail("xx5882@naver.com")
                .socialType(SocialType.NAVER)
                .socialId("id")
                .user(user)
                .build();
        SocialProfile kakao = SocialProfile.builder()
                .id(2L)
                .socialEmail("xx5882@naver.com")
                .socialType(SocialType.KAKAO)
                .socialId("id")
                .user(user)
                .build();
        SocialProfile google = SocialProfile.builder()
                .id(3L)
                .socialEmail("dydydade@gmail.com")
                .socialType(SocialType.GOOGLE)
                .socialId("sub")
                .user(user)
                .build();

        userRepository.save(user);
//        socialProfileRepository.save(naver);
//        socialProfileRepository.save(kakao);
//        socialProfileRepository.save(google);
    }
}
