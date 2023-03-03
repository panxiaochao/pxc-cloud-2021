package io.github.panxiaochao.gateway.handler;

import io.github.panxiaochao.common.response.R;
import io.github.panxiaochao.common.utils.StrUtil;
import io.github.panxiaochao.gateway.utils.WebFluxUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * {@code GatewayGlobalExceptionHandler}
 * <p> description: 网关全局统一异常处理
 *
 * @author Lypxc
 * @since 2023-02-08
 */
@Order(-1)
@Configuration
public class GatewayGlobalExceptionHandler implements ErrorWebExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayGlobalExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        ServerHttpResponse response = exchange.getResponse();
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(throwable);
        }
        String msg;
        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        RequestPath requestPath = exchange.getRequest().getPath();
        if (throwable instanceof NotFoundException) {
            msg = requestPath.value() + "服务未找到";
        } else if (StrUtil.containsIgnoreCase(throwable.getMessage(), "connection refused")) {
            msg = "目标服务拒绝连接";
        } else if (throwable instanceof TimeoutException) {
            msg = "访问服务超时";
        } else if (throwable instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
            code = responseStatusException.getRawStatusCode();
            msg = responseStatusException.getMessage();
        } else {
            msg = "网关内部服务器错误";
        }
        final String errorMsg = String.format("网关异常处理, 请求路径: %s, 异常类: %s, 异常信息: %s",
                requestPath, throwable.getClass(), throwable.getMessage());
        LOGGER.error(errorMsg, throwable);
        return WebFluxUtil.webFluxResponseWriter(response, msg, code);
    }

    /**
     * 兜底处理异常
     */
    @ExceptionHandler(value = Exception.class)
    public R<Object> defaultExceptionHandler(ServerWebExchange exchange, Throwable throwable) {
        RequestPath requestPath = exchange.getRequest().getPath();
        final String errorMsg = String.format("网关异常处理, 请求路径: %s, 异常类: %s, 异常信息: %s",
                requestPath, throwable.getClass(), throwable.getMessage());
        LOGGER.error(errorMsg, throwable);
        return R.fail();
    }
}
