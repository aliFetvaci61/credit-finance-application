package com.alifetvaci.credit.gateway.redis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Session implements Serializable {

    @Serial
    private static final long serialVersionUID = -4629854556123464081L;

    private String identificationNumber;
    private String firstname;
    private String lastname;

}