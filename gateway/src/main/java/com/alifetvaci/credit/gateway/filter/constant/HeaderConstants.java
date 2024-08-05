package com.alifetvaci.credit.gateway.filter.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderConstants {

    public static final String firstName = "X-Firstname";

    public static final String lastName = "X-Lastname";

    public static final String identificationNumber = "X-Identitification-Number";
}
