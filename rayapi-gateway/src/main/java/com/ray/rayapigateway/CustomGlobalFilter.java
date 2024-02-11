package com.ray.rayapigateway;

import com.ray.rayapiclientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    //白名单
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识:" + request.getId());
        log.info("请求路径:" + request.getPath().value());
        log.info("请求方法" + request.getMethod());
        log.info("请求参数" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址" + sourceAddress);
        log.info("请求来源地址" + request.getRemoteAddress());

        //2.访问控制 -黑白名单
        //拿到响应对象
        ServerHttpResponse response = exchange.getResponse();
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            // 设置响应状态码为403 Forbidden(禁止访问)
            response.setStatusCode(HttpStatus.FORBIDDEN);
            //返回处理完成的响应
            return response.setComplete();
        }
        // 3.用户鉴权(判断ak、sk是否合法)

        // 从请求头中获取参数
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        // todo 实际情况应该是去数据库中查是否已分配给用户

        if (!accessKey.equals("lilray")) {
            return handleNoAuth(response);
        }
        // 直接校验如果随机数大于1万，则抛出异常，并提示"无权限"
        if (Long.parseLong(nonce) > 10000) {
            return handleNoAuth(response);
        }

        // 时间和当前时间不能超过5分钟
        // 首先,获取当前时间的时间戳,以秒为单位
        // System.currentTimeMillis()返回当前时间的毫秒数，除以1000后得到当前时间的秒数。
        Long currentTime = System.currentTimeMillis() / 1000;
        // 定义一个常量FIVE_MINUTES,表示五分钟的时间间隔(乘以60,将分钟转换为秒,得到五分钟的时间间隔)。
        final Long FIVE_MINUTES = 60 * 5L;
        // 判断当前时间与传入的时间戳是否相差五分钟或以上
        // Long.parseLong(timestamp)将传入的时间戳转换成长整型
        // 然后计算当前时间与传入时间戳之间的差值(以秒为单位),如果差值大于等于五分钟,则返回true,否则返回false
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            // 如果时间戳与当前时间相差五分钟或以上，调用handleNoAuth(response)方法进行处理
            return handleNoAuth(response);
        }

        // todo 实际情况中是从数据库中查出 secretKey
        String serverSign = SignUtils.genSign(body, "abcdefgh");
        // 如果生成的签名不一致，则抛出异常，并提示"无权限"
        if (!sign.equals(serverSign)) {
            return handleNoAuth(response);
        }

        //4.判断请求的模拟接口信息是否存在
        // todo 从数据库中查询模拟接口是否存在，以及请求方法是否匹配(还可以校验请求参数)

        // 5.请求转发，调用模拟接口
        Mono<Void> filter = chain.filter(exchange);
        // 6.调用成功之后要输入一个响应日志
        return handleResponse(exchange,chain);

    }

    //处理响应
    public Mono<Void> handleResponse(ServerWebExchange exchange,GatewayFilterChain chain){
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            HttpStatus statusCode = originalResponse.getStatusCode();

            if(statusCode == HttpStatus.OK){
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 7.todo 调用成功，接口调用次数 + 1  invokeCount
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);

                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                //打印日志
                                log.info("响应结果" + data);

                                return bufferFactory.wrap(content);

                            }));
                        } else {
                            //调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        }catch (Exception e){
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        // 设置响应状态码为403 Forbidden(禁止访问)
        response.setStatusCode(HttpStatus.FORBIDDEN);
        //返回处理完成的响应
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        // 用于返回错误码 500(调用失败)。
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        //返回处理完成的响应
        return response.setComplete();
    }
}