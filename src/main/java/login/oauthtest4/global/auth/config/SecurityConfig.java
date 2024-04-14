package login.oauthtest4.global.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import login.oauthtest4.domain.user.repository.UserRepository;
import login.oauthtest4.domain.user.service.UserRefreshTokenService;
import login.oauthtest4.global.auth.jwt.exception.JwtExceptionHandlingFilter;
import login.oauthtest4.global.auth.jwt.filter.JwtAuthenticationProcessingFilter;
import login.oauthtest4.global.auth.jwt.service.JwtService;
import login.oauthtest4.global.auth.login.exception.LoginExceptionHandlingFilter;
import login.oauthtest4.global.auth.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import login.oauthtest4.global.auth.login.handler.LoginFailureHandler;
import login.oauthtest4.global.auth.login.handler.LoginSuccessHandler;
import login.oauthtest4.global.auth.login.service.LoginService;
import login.oauthtest4.global.auth.oauth2.exception.OAuth2ExceptionHandlingFilter;
import login.oauthtest4.global.auth.oauth2.handler.OAuth2LoginFailureHandler;
import login.oauthtest4.global.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import login.oauthtest4.global.auth.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리
 * JwtAuthenticationProcessingFilter는 AccessToken, RefreshToken 재발급
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserRefreshTokenService userRefreshTokenService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                // 세션 사용하지 않으므로 STATELESS로 설정
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //== URL별 권한 관리 옵션 ==//
                .authorizeHttpRequests(auth -> auth
                        // 아이콘, css, js 관련
                        // 기본 페이지, css, image, js 하위 폴더에 있는 자료들은 모두 접근 가능
                        .requestMatchers("/","/sign-up","/css/**","/images/**","/js/**","/favicon.ico").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll() // 회원가입 요청은 인증 대상 제외
                        .anyRequest().authenticated()
                )
                // 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
                // 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
                // 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
                .addFilterAfter(jwtAuthenticationProcessingFilter(), LogoutFilter.class)
                .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), JwtAuthenticationProcessingFilter.class)

                // 필터 단에서 발생하는 예외를 처리하기 위한 예외 핸들링 필터 등록
                .addFilterBefore(jwtExceptionHandlingFilter(), JwtAuthenticationProcessingFilter.class)
                .addFilterBefore(oauth2ExceptionHandlingFilter(), JwtExceptionHandlingFilter.class)
                .addFilterBefore(loginExceptionHandlingFilter(), OAuth2ExceptionHandlingFilter.class)

                //== 소셜 로그인 설정 ==//
                .oauth2Login(oauth2 -> oauth2
                                .authorizationEndpoint(authorizationEndpoint  ->
                                        authorizationEndpoint
                                            .baseUri("/api/v1/auth/social/login")
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                );

        return http.build();
    }

    /**
     * AuthenticationManager 설정 후 등록
     * PasswordEncoder를 사용하는 AuthenticationProvider 지정 (PasswordEncoder는 위에서 등록한 PasswordEncoder 사용)
     * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
     * UserDetailsService는 커스텀 LoginService로 등록
     * 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return ProviderManager)
     *
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }

    /**
     * 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록
     */
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService, userRefreshTokenService);
    }

    /**
     * 로그인 실패 시 호출되는 LoginFailureHandler 빈 등록
     */
    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    /**
     * CustomJsonUsernamePasswordAuthenticationFilter 빈 등록
     * 커스텀 필터를 사용하기 위해 만든 커스텀 필터를 Bean으로 등록
     * setAuthenticationManager(authenticationManager())로 위에서 등록한 AuthenticationManager(ProviderManager) 설정
     * 로그인 성공 시 호출할 handler, 실패 시 호출할 handler로 위에서 등록한 handler 설정
     */
    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return customJsonUsernamePasswordLoginFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService, userRepository, userRefreshTokenService, objectMapper);
        return jwtAuthenticationFilter;
    }

    /**
     * Filter 단에서 던져지는 Jwt 관련 예외를 핸들링하기 위한 필터
     * @return
     */
    @Bean
    public JwtExceptionHandlingFilter jwtExceptionHandlingFilter() {
        return new JwtExceptionHandlingFilter(objectMapper);
    }

    /**
     * Filter 단에서 던져지는 OAuth2 관련 예외를 핸들링하기 위한 필터
     * @return
     */
    @Bean
    public OAuth2ExceptionHandlingFilter oauth2ExceptionHandlingFilter() {
        return new OAuth2ExceptionHandlingFilter(objectMapper);
    }

    /**
     * Filter 단에서 던져지는 Login 관련 예외를 핸들링하기 위한 필터
     * @return
     */
    @Bean
    public LoginExceptionHandlingFilter loginExceptionHandlingFilter() {
        return new LoginExceptionHandlingFilter(objectMapper);
    }
}
