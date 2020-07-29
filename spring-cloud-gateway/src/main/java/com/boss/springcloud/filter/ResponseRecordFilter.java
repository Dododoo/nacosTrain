package com.boss.springcloud.filter;


import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

@Slf4j
@Component
public class ResponseRecordFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(ResponseRecordFilter.class);
    private static Joiner joiner = Joiner.on("");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
        ServerHttpResponseDecorator serverHttpResponseDecorator = new ServerHttpResponseDecorator(serverHttpResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (getStatusCode().equals(HttpStatus.OK) && body instanceof Flux) {
                    // 获取ContentType
                    String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    if (StringUtils.isNotBlank(originalResponseContentType)) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            List<String> list = Lists.newArrayList();
                            dataBuffers.forEach(dataBuffer -> {
                                try {
                                    byte[] content = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(content);
                                    DataBufferUtils.release(dataBuffer);

                                    list.add(new String(content, "utf-8"));
                                } catch (Exception e) {
                                    logger.info("失败原因：{}", Throwables.getStackTraceAsString(e));
                                }
                            });
                            String responseData = joiner.join(list);
                            logger.info(responseData);
                            byte[] uppedContent = new String(responseData.getBytes(), Charset.forName("UTF-8")).getBytes();
                            serverHttpResponse.getHeaders().setContentLength(uppedContent.length);
                            return dataBufferFactory.wrap(uppedContent);
                        }));
                    }
                }
                return super.writeWith(body);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                logger.info(writeWith(Flux.from(body).flatMapSequential(p -> p)) + "");
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
        logger.debug(exchange.mutate().response(serverHttpResponseDecorator).build() + "");
        return chain.filter(exchange.mutate().response(serverHttpResponseDecorator).build());
    }

    @Override
    public int getOrder() {
        return -98;
    }
}
