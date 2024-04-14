package login.oauthtest4.domain.terms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import login.oauthtest4.domain.terms.dto.LatestTermsResponse;
import login.oauthtest4.domain.terms.dto.TermsCreateRequest;
import login.oauthtest4.domain.terms.dto.TermsCreateResponse;
import login.oauthtest4.domain.terms.service.TermsService;
import login.oauthtest4.global.response.ResultCode;
import login.oauthtest4.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Terms API", description = "이용약관 관리 API")
@RestController
@RequestMapping("/api/v1/terms")
@RequiredArgsConstructor
public class TermsRestController {

    private final TermsService termsService;

    @PostMapping
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "신규 이용 약관 등록", description = "신규 이용 약관 등록 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이용 약관을 등록하였습니다.", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "입력 값이 유효하지 않습니다."),
            @ApiResponse(responseCode = "403", description = "유효하지 않은 토큰입니다.")
    })
    public ResponseEntity<ResultResponse> createTerms(
            @RequestBody @Valid TermsCreateRequest termsCreateRequest
    ) {
        TermsCreateResponse termsCreateResponse = termsService.createTerms(termsCreateRequest);
        ResultResponse result = ResultResponse.of(ResultCode.TERMS_REGISTRATION_SUCCESS, termsCreateResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/latest")
    @Operation(summary = "최신 이용 약관 조회", description = "최신 이용 약관 목록 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "최신 이용 약관을 조회하였습니다.", useReturnTypeSchema = true,
            content = {@Content(schema = @Schema(implementation = LatestTermsResponse.class))}
    ),
    })
    public ResponseEntity<ResultResponse> findLatestVersionOfEachTermsType() {
        LatestTermsResponse latestTermsResponse = termsService.findLatestVersionOfEachTermsType();
        ResultResponse result = ResultResponse.of(ResultCode.TERMS_LIST_RETRIEVED_SUCCESS, latestTermsResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
}
