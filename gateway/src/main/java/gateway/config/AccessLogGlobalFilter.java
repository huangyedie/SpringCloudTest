package gateway.config;

import brave.Tracer;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import org.apache.commons.lang.ObjectUtils;
import gateway.model.GatewayLog;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author : huangmingshun
 * @description :
 * @Date : 2022/8/24 11:20
 */
@Slf4j
@Component
public class AccessLogGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private Tracer tracer;

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayLog gatewayLog = new GatewayLog();

        ServerHttpRequest request = exchange.getRequest();
        //???????????????ip???url???method???body
        String requestPath = request.getPath().pathWithinApplication().value();
        String clientIp = request.getRemoteAddress().getHostString();
        String scheme = request.getURI().getScheme();
        String method = request.getMethodValue();
        gatewayLog.setSchema(scheme);
        gatewayLog.setRequestMethod(method);
        gatewayLog.setRequestPath(requestPath);
        gatewayLog.setRequestTime(simpleDateFormat.format(new Date().getTime()));
        gatewayLog.setIp(clientIp);
        //????????????ID
        gatewayLog.setTrackId(tracer.currentSpan().context().traceIdString());

        MediaType contentType = request.getHeaders().getContentType();
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType) || MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            return writeBodyLog(exchange, chain, gatewayLog);
        } else {
            return writeBasicLog(exchange, chain, gatewayLog);
        }
    }

    private Mono<Void> writeBasicLog(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog accessLog) {
        StringBuilder builder = new StringBuilder();
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            builder.append(entry.getKey()).append("=").append(StringUtils.join(entry.getValue(), ","));
        }
        accessLog.setRequestBody(builder.toString());

        //    ???????????????
        ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, accessLog);
        return chain.filter(exchange.mutate().response(decoratedResponse).build())
                .then(Mono.fromRunnable(() -> {
                    //????????????
                    writeAccessLog(accessLog);
                }));
    }


    /**
     * ??????request body ????????????????????????
     *
     * @param exchange
     * @param chain
     * @param gatewayLog
     * @return
     */
    private Mono writeBodyLog(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog gatewayLog) {
        ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                .flatMap(body -> {
                    gatewayLog.setRequestBody(body);
                    return Mono.just(body);
                });

        // ?????? BodyInsert ?????? body(????????????body), ?????? request body ??????????????????
        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());

        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);

        return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    // ??????????????????
                    ServerHttpRequest decoratedRequest = requestDecorate(exchange, headers, outputMessage);

                    // ??????????????????
                    ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, gatewayLog);

                    // ???????????????
                    return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build())
                            .then(Mono.fromRunnable(() -> {
                                // ????????????
                                writeAccessLog(gatewayLog);
                            }));
                }));
    }


    /**
     * ????????????
     *
     * @param gatewayLog
     */
    private void writeAccessLog(GatewayLog gatewayLog) {
        log.info(JSONObject.toJSON(gatewayLog).toString());
    }

    /**
     * ?????????????????????????????? headers
     *
     * @param exchange
     * @param headers
     * @param outputMessage
     * @return
     */
    private ServerHttpRequestDecorator requestDecorate(ServerWebExchange exchange, HttpHeaders headers,
                                                       CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
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
    }

    /**
     * ??????????????????
     *
     * @param exchange
     * @param gatewayLog
     * @return
     */
    private ServerHttpResponseDecorator recordResponseLog(ServerWebExchange exchange, GatewayLog gatewayLog) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();

        return new ServerHttpResponseDecorator(response) {
            @SneakyThrows
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    String responseTime = simpleDateFormat.format(new Date().getTime());
                    gatewayLog.setResponseTime(responseTime);
                    // ??????????????????
                    long executeTime = (simpleDateFormat.parse(responseTime).getTime() - simpleDateFormat.parse(gatewayLog.getRequestTime()).getTime());
                    gatewayLog.setExecuteTime(executeTime);

                    // ?????????????????????????????? json ?????????
                    String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);

                    if (ObjectUtils.equals(this.getStatusCode(), HttpStatus.OK)
                            && StringUtils.isNotBlank(originalResponseContentType)
                            && originalResponseContentType.contains("application/json")) {

                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

                            // ???????????????????????????????????????????????????
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);

                            // ???????????????
                            DataBufferUtils.release(join);
                            String responseResult = new String(content, StandardCharsets.UTF_8);
                            gatewayLog.setResponseBody(responseResult);

                            return bufferFactory.wrap(content);
                        }));
                    }
                }
                return super.writeWith(body);
            }
        };
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
