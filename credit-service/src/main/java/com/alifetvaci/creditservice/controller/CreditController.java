package com.alifetvaci.creditservice.controller;


import com.alifetvaci.creditservice.api.BaseController;
import com.alifetvaci.creditservice.controller.request.CreditCreateRequest;
import com.alifetvaci.creditservice.controller.response.BaseApiResponse;
import com.alifetvaci.creditservice.controller.response.CreditResponse;
import com.alifetvaci.creditservice.service.CreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CreditController extends BaseController {

    private final CreditService service;

    @PostMapping("api/v1/credit")
    public BaseApiResponse<Void> createCredit(@RequestHeader("X-Identitification-Number") String identificationNumber,  @RequestBody @Valid CreditCreateRequest request) {
        service.createCredit(request,identificationNumber);
        return success(null);
    }

    @GetMapping("api/v1/credit")
    public BaseApiResponse<List<CreditResponse>> getCredits(@RequestHeader("X-Identitification-Number") String identificationNumber) {
        return success(service.getCredits(identificationNumber));
    }

    @GetMapping("api/v1/credit/{creditId}")
    public BaseApiResponse<CreditResponse> getCredit(@RequestHeader("X-Identitification-Number") String identificationNumber,
                                                           @Valid @PathVariable int creditId) {
        return success(service.getCredit(identificationNumber, creditId));
    }

    @GetMapping("api/v1/credit/{creditId}/installment")
    public BaseApiResponse<CreditResponse> getCreditInstallment(@RequestHeader("X-Identitification-Number") String identificationNumber,
                                                           @Valid @PathVariable int creditId) {
        return success(service.getCreditInstallments(identificationNumber,creditId));
    }

    @PostMapping("api/v1/credit/{creditId}/installment/{installmentId}")
    public BaseApiResponse<Void> getCreditInstallment(@RequestHeader("X-Identitification-Number") String identificationNumber,
                                                                      @Valid @PathVariable int creditId,
                                                                      @Valid @PathVariable int installmentId) {
        service.payInstallment(identificationNumber, creditId, installmentId);
        return success(null);
    }
}
