package com.alifetvaci.credit.gateway.filter;

import com.alifetvaci.credit.gateway.exception.UnauthorizedException;
import com.alifetvaci.credit.gateway.filter.constant.HeaderConstants;
import com.alifetvaci.credit.gateway.redis.model.Session;
import com.alifetvaci.credit.gateway.service.JwtService;
import com.alifetvaci.credit.gateway.service.SessionService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenFilter implements GlobalFilter {

    private final JwtService jwtService;

    private final SessionService sessionService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        boolean tokenRequired = (boolean) route.getMetadata().get("tokenRequired");
        if (tokenRequired) {
            String token = getByKeyFromHeaders(HttpHeaders.AUTHORIZATION, exchange.getRequest());
            final String authToken = token.substring(7);
            Claims claims = jwtService.isTokenValid(authToken);
            if (Objects.isNull(claims)) {
                return Mono.error(new UnauthorizedException());
            }
            String uuid = claims.get("sub", String.class);
            Session session = sessionService.getValue(uuid);
            if(Objects.isNull(session)){
                return Mono.error(new UnauthorizedException());
            }
            addInformationHeadersFromClaims(exchange, session);
            return chain.filter(exchange);

        }
        else {
            return chain.filter(exchange);
        }

    }

    private void addInformationHeadersFromClaims(ServerWebExchange exchange, Session session) {
        exchange.getRequest().mutate()
                .header(HeaderConstants.firstName, session.getFirstname() )
                .header(HeaderConstants.lastName, session.getLastname())
                .header(HeaderConstants.identificationNumber, session.getIdentificationNumber())
                .build();

    }

    private String getByKeyFromHeaders(String key, ServerHttpRequest request) {
        final var header = request.getHeaders().get(key);
        if (Objects.nonNull(header)) {
            return header.get(0);
        }
        return null;
    }

}
