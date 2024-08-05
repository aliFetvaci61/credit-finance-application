package com.alifetvaci.credit.userservice.delegate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
public class CustomApiResponse<T> {

    @NotNull
    private Boolean success;

    @Valid
    private T data;

}
