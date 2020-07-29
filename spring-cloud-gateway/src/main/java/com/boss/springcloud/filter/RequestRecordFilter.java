package com.boss.springcloud.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RequestRecordFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestRecordFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取用户传来的数据类型
        MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
        ServerRequest serverRequest = new DefaultServerRequest(exchange);

        // 如果是json格式，将body内容转化为object or map 都可
        if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            Mono<Object> modifiedBody = serverRequest.bodyToMono(Object.class).flatMap(body -> {
                recordLog(exchange.getRequest(), body);
                return Mono.just(body);
            });
            return getVoidMono(exchange, chain, Object.class, modifiedBody);
        }
        // 如果是表单请求
        else if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
                recordLog(exchange.getRequest(), body);
                return Mono.just(body);
            });
            return getVoidMono(exchange, chain, String.class, modifiedBody);
        }
        recordLog(exchange.getRequest(), "");
        return chain.filter(exchange.mutate().request(exchange.getRequest()).build());
    }

    private Mono<Void> getVoidMono(ServerWebExchange exchange, GatewayFilterChain chain, Class outClass, Mono<?> modifiedBody) {
        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, outClass);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);

        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public HttpHeaders getHeaders() {
                    long contentLength = headers.getContentLength();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(super.getHeaders());
                    if (contentLength > 0) {
                        httpHeaders.setContentLength(contentLength);
                    } else {
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    }
                    return httpHeaders;
                }

                @Override
                public Flux<DataBuffer> getBody() {
                    return outputMessage.getBody();
                }
            };
            return chain.filter(exchange.mutate().request(decorator).build());
        }));
    }

    private void recordLog(ServerHttpRequest request, Object body) {

        StringBuilder builder = new StringBuilder(" request url: ");
        builder.append(request.getURI().getRawPath());
        HttpMethod method = request.getMethod();
        if (null != method) {
            builder.append(", method: ").append(method.name());
        }
        builder.append(", header { ");
        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            builder.append(entry.getKey()).append(":").append(StringUtils.join(entry.getValue(), ",")).append(",");
        }
        builder.append("} param: ");
        if (null != method && HttpMethod.GET.matches(method.name())) {
            MultiValueMap<String, String> queryParams = request.getQueryParams();
            for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
                builder.append(entry.getKey()).append("=").append(StringUtils.join(entry.getValue(), ",")).append(",");
            }
        } else {
            builder.append(body);
        }
        logger.info(builder.toString());
    }

    @Override
    public int getOrder() {
        return -99;
    }
}
