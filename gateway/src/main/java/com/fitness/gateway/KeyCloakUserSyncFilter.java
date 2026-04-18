package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.function.Consumer;

import static java.util.Optional.empty;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //String userId=exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token= exchange.getRequest().getHeaders().getFirst("Authorization");
        RegisterRequest registerRequest= getUserDetails(token);
        log.info("Token: {}", token);
        log.info("RegisterRequest: {}", registerRequest);
        String userId=registerRequest.getKeycloakId();
        log.info("UserId: {}", userId);

        if(userId!=null && token!=null){
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist->{
                        if (!exist) {
                            if (registerRequest != null) {
                                return userService.registerUser(registerRequest)
                                        .then(Mono.just(true)); // continue flow
                            } else {
                                return Mono.just(false);
                            }
                        } else {
                            log.info("User already exists, skipping sync");
                            return Mono.just(true);
                        }
                    })
                    .then(Mono.defer(()->{
                        ServerHttpRequest mutatedRequest= exchange.getRequest().mutate()
                                .header("X-User-ID", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
        try{
            String tokenwithoutbearer=token.replace("Bearer","").trim();
            SignedJWT signedjwt=SignedJWT.parse(tokenwithoutbearer);
            JWTClaimsSet claims=signedjwt.getJWTClaimsSet();
            RegisterRequest request= new RegisterRequest();
            request.setEmail(claims.getStringClaim("email"));
            request.setKeycloakId(claims.getStringClaim("sub"));
            request.setPassword("dummy@123");
            request.setFirstName(claims.getStringClaim("given_name"));
            request.setLastName(claims.getStringClaim("family_name"));
            return request;



        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
