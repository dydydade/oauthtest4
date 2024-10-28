package login.tikichat.domain.host.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import login.tikichat.domain.host.dto.FindHostDto;
import login.tikichat.domain.host.service.HostService;
import login.tikichat.global.auth.UserDetailInfo;
import login.tikichat.global.response.ResultCode;
import login.tikichat.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT")
@RestController
@Tag(name = "Host API", description = "호스트 API")
@RequestMapping("/api/v1/hosts")
@RequiredArgsConstructor
public class HostController {

    private final HostService hostService;

    @GetMapping("")
    @Operation(summary = "팔로워가 팔로우한 호스트 목록 조회", description = "팔로워가 팔로우하고 있는 호스트 목록을 조회하는 API 입니다. followerId 파라미터를 넘기지 않으면 인증된 사용자(나)가 팔로우하고 있는 호스트 명단이 조회됩니다.")
    public ResponseEntity<ResultResponse> findFollowedHosts(
            @Parameter(description = "팔로워 ID (필수값 아님)")
            @RequestParam(name = "followerId", required = false) Long followerId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.FOLLOWED_HOSTS_FOUND,
                followerId != null ? this.hostService.findTargetFollowerHosts(followerId) : this.hostService.findMyFollowedHosts(user.getUserId())
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/{hostId}")
    @Operation(summary = "호스트 상세 정보 조회", description = "호스트의 상세 정보를 조회하는 API 입니다.")
    public ResponseEntity<ResultResponse> findHostProfile(
            @PathVariable("hostId") Long hostId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.HOST_PROFILE_FOUND,
                this.hostService.getHostProfile(hostId, user.getUserId())
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @PostMapping("/{hostId}/follow")
    @Operation(summary = "호스트 팔로우", description = "호스트를 팔로우하는 API 입니다.")
    public ResponseEntity<ResultResponse> followTargetHost(
            @PathVariable("hostId") Long hostId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.HOST_FOLLOWED,
                hostService.follow(hostId, user.getUserId())
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @DeleteMapping("/{hostId}/follow")
    @Operation(summary = "호스트 팔로우 취소", description = "호스트 팔로우를 취소하는 API 입니다.")
    public ResponseEntity<ResultResponse> unfollowTargetHost(
            @PathVariable("hostId") Long hostId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.HOST_UNFOLLOWED,
                hostService.unfollow(hostId, user.getUserId())
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/{hostId}/followers")
    @Operation(summary = "팔로워 목록 조회", description = "대상 호스트를 팔로우한 팔로워 목록을 조회하는 API 입니다.")
    public ResponseEntity<ResultResponse> findTargetHostFollowers(
            @PathVariable("hostId") Long hostId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.TARGET_HOST_FOLLOWERS_FOUND,
                this.hostService.findTargetHostFollowers(hostId)
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("")
    @Operation(summary = "호스트 조회(키워드)", description = "검색 키워드를 통해 해당하는 호스트를 조회하는 API 입니다.")
    public ResponseEntity<ResultResponse> searchHostsByHostName(
            @RequestBody
            @Valid
            @Parameter(required = true)
            FindHostDto.FindHostReq findHostReq,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.FIND_HOSTS_SUCCESS,
                this.hostService.searchHostsByHostName(findHostReq.searchKeyword())
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
}
