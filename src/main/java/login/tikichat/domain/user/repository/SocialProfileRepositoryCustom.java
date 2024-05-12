package login.tikichat.domain.user.repository;

import login.tikichat.domain.user.model.SocialProfile;
import login.tikichat.domain.user.model.SocialType;

import java.util.Optional;

public interface SocialProfileRepositoryCustom {

    Optional<SocialProfile> findBySocialEmailAndSocialTypeWithUser(String email, SocialType socialType);
}
