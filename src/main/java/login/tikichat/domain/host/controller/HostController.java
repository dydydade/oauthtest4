package login.tikichat.domain.host.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import login.tikichat.domain.host.service.HostFollowStatusService;
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

    private final HostFollowStatusService hostFollowStatusService;

    @GetMapping("")
    @Operation(summary = "내가 팔로우한 호스트 목록 조회", description = "내가 팔로우하고 있는 호스트 목록을 조회하는 API 입니다.")
    public ResponseEntity<ResultResponse> findFollowedHosts(
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.FIND_FOLLOWED_HOSTS_INFO_SUCCESS,
                this.hostFollowStatusService.findFollowedHosts(user.getUserId())
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
                ResultCode.FIND_HOST_PROFILE_SUCCESS,
                this.hostFollowStatusService.getHostProfile(hostId, user.getUserId())
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @PostMapping("/{hostId}/follow")
    @Operation(summary = "호스트 팔로우", description = "호스트를 팔로우하는 API 입니다.")
    public ResponseEntity<ResultResponse> subscribeTargetHost(
            @PathVariable("hostId") Long hostId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.SUBSCRIBE_HOST_SUCCESS,
                hostFollowStatusService.subscribe(hostId, user.getUserId())
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @DeleteMapping("/{hostId}/follow")
    @Operation(summary = "호스트 팔로우 취소", description = "호스트 팔로우를 취소하는 API 입니다.")
    public ResponseEntity<ResultResponse> unsubscribeTargetHost(
            @PathVariable("hostId") Long hostId,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.UNSUBSCRIBE_HOST_SUCCESS,
                hostFollowStatusService.unsubscribe(hostId, user.getUserId())
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
                ResultCode.FIND_FOLLOWED_HOSTS_INFO_SUCCESS,
                this.hostFollowStatusService.findTargetHostFollowers(hostId)
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
