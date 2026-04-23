package kr.co.dbinc.cursorstudy.domain.claim.controller;

import kr.co.dbinc.cursorstudy.common.web.CommonResponse;
import kr.co.dbinc.cursorstudy.domain.claim.dto.ClaimRequestDto;
import kr.co.dbinc.cursorstudy.domain.claim.dto.ClaimResponseDto;
import kr.co.dbinc.cursorstudy.domain.claim.service.ClaimService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 클레임 REST 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/claims")
public class ClaimController {

    private final ClaimService claimService;

    /**
     * 클레임을 접수한다.
     *
     * @param request 접수 요청 본문
     * @return 공통 응답 래핑된 클레임 정보
     */
    @PostMapping
    public CommonResponse<ClaimResponseDto> register(@Valid @RequestBody ClaimRequestDto request) {
        return CommonResponse.ok(claimService.registerClaim(request));
    }

    /**
     * 클레임 단건을 조회한다.
     *
     * @param claimSeq 클레임 PK
     * @return 공통 응답 래핑된 클레임 정보
     */
    @GetMapping("/{claimSeq}")
    public CommonResponse<ClaimResponseDto> get(@PathVariable Long claimSeq) {
        return CommonResponse.ok(claimService.getClaim(claimSeq));
    }
}
