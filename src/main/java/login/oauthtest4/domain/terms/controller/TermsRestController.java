package login.oauthtest4.domain.terms.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import login.oauthtest4.domain.terms.dto.LatestTermsResponse;
import login.oauthtest4.domain.terms.dto.TermsCreateRequest;
import login.oauthtest4.domain.terms.dto.TermsCreateResponse;
import login.oauthtest4.domain.terms.service.TermsService;
import login.oauthtest4.global.response.ResultCode;
import login.oauthtest4.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Terms API", description = "이용약관 관리 API")
@RestController
@RequestMapping("/api/v1/terms")
@RequiredArgsConstructor
public class TermsRestController {

    private final TermsService termsService;

    @PostMapping
    public ResponseEntity<Object> createTerms(@RequestBody TermsCreateRequest termsCreateRequest) {
        TermsCreateResponse termsCreateResponse = termsService.createTerms(termsCreateRequest);
        ResultResponse result = ResultResponse.of(ResultCode.TERMS_REGISTRATION_SUCCESS, termsCreateResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/latest")
    public ResponseEntity<Object> findLatestVersionOfEachTermsType() {
        List<LatestTermsResponse> latestTermsResponses = termsService.findLatestVersionOfEachTermsType();
        ResultResponse result = ResultResponse.of(ResultCode.TERMS_LIST_RETRIEVED_SUCCESS, latestTermsResponses);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
}
