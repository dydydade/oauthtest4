package login.oauthtest4.domain.user.repository;

import login.oauthtest4.domain.user.model.SocialProfile;
import login.oauthtest4.domain.user.model.SocialType;

import java.util.Optional;

public interface SocialProfileRepositoryCustom {

    Optional<SocialProfile> findBySocialEmailAndSocialTypeWithUser(String email, SocialType socialType);
}
