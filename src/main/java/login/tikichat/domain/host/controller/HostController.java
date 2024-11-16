package login.tikichat.domain.host.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import login.tikichat.domain.host.dto.FindFollowerDto;
import login.tikichat.domain.host.dto.FindHostDto;
import login.tikichat.domain.host.dto.HostFollowDto;
import login.tikichat.domain.host.dto.HostProfileDto;
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

    @GetMapping("/{hostId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "호스트 상세 정보 조회", description = "호스트의 상세 정보를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "호스트 프로필 정보를 조회하였습니다.",
                    content = {@Content(schema = @Schema(implementation = HostProfileDto.HostProfileRes.class))}
            )
    })
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
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "호스트 팔로우", description = "호스트를 팔로우하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "호스트를 팔로우하였습니다.",
                    content = {@Content(schema = @Schema(implementation = HostFollowDto.HostFollowRes.class))}
            )
    })
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
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "호스트 팔로우 취소", description = "호스트 팔로우를 취소하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "호스트 팔로우를 취소하였습니다.",
                    content = {@Content(schema = @Schema(implementation = HostFollowDto.HostFollowRes.class))}
            )
    })
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
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "팔로워 목록 조회", description = "대상 호스트를 팔로우한 팔로워 목록을 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대상 호스트를 팔로우하는 팔로워 목록을 조회하였습니다.",
                    content = {@Content(schema = @Schema(implementation = FindFollowerDto.FindFollowerRes.class))}
            )
    })
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
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "호스트 조회(키워드)", description = "호스트를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "호스트 목록을 조회하였습니다.",
                    content = {@Content(schema = @Schema(implementation = FindHostDto.FindHostRes.class))}
            )
    })
    public ResponseEntity<ResultResponse> findHosts(
            @Valid FindHostDto.FindHostReq findHostReq,
            @AuthenticationPrincipal UserDetailInfo user
    ) {
        ResultResponse result = ResultResponse.of(
                ResultCode.FIND_HOSTS_SUCCESS,
                findHostReq.isFetchMyFollowedHost() ? this.hostService.findMyFollowedHosts(user.getUserId()) : this.hostService.findHosts(findHostReq)
        );
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
}
