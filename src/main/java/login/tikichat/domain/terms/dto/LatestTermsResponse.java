package login.tikichat.domain.terms.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestTermsResponse {

    @Schema(description = "최신 이용약관 목록")
    private List<LatestTermsDto> termsDtos;
}
