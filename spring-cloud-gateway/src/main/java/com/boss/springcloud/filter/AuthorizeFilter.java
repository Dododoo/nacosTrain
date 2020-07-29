package com.boss.springcloud.filter;

import com.alibaba.fastjson.JSON;
import com.boss.springcloud.exception.TokenAuthenticationException;
import com.boss.springcloud.service.ResponseResult;
import com.boss.springcloud.util.JWTUtil;
import com.boss.springcloud.util.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Set;

@Slf4j
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Value("${secreteKey:123456}")
    private String secreteKey;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        String uri = serverHttpRequest.getURI().getPath();
        String user = serverHttpRequest.getBody().toString();
        log.info(user);
        //设置登录请求白名单
        if (uri.indexOf("auth/login") >= 0) {
            return chain.filter(exchange);
        }

        //验证是否存在token
        String token = serverHttpRequest.getHeaders().getFirst("token");
        log.info(token);
        if (StringUtils.isBlank(token)) {
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return getVoidMono(serverHttpResponse, ResponseCodeEnum.TOKEN_MISSION);
        }
        //验证token是否有效
        try {
            JWTUtil.verifyToken(token, secreteKey);
        }catch (TokenAuthenticationException ex) {
            return getVoidMono(serverHttpResponse, ResponseCodeEnum.TOKEN_INVALID);
        }catch (Exception ex) {
            return getVoidMono(serverHttpResponse, ResponseCodeEnum.UNKNOWN_ERROR);
        }
        //根据token获取用户信息
        String username = JWTUtil.getUserInfo(token);
        log.info(username);
        //验证该用户是否有访问当前url资源的权限
        redisTemplate.opsForValue().set("username", "system");
        Set<String> keys = redisTemplate.opsForHash().keys(username);
        for(String key1 : keys){
            log.info("key1=========="+keys);
        }
        log.info(uri);
        if (!redisTemplate.opsForHash().hasKey(username, uri)) {
            return getVoidMono(serverHttpResponse, ResponseCodeEnum.AUTH_MISSION);
        }
        //在请求头中添加用户username
        ServerHttpRequest mutableReq = serverHttpRequest.mutate().header("username", username).build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();

        return chain.filter(mutableExchange);
    }

    private Mono<Void> getVoidMono(ServerHttpResponse serverHttpResponse, ResponseCodeEnum responseCodeEnum) {
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        ResponseResult responseResult = ResponseResult.error(responseCodeEnum.getCode(), responseCodeEnum.getMessage());
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(JSON.toJSONString(responseResult).getBytes());
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
