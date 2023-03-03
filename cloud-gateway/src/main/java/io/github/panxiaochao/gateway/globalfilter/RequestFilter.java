package io.github.panxiaochao.gateway.globalfilter;

import io.github.panxiaochao.common.utils.LocalhostUtil;
import io.github.panxiaochao.common.utils.SpringContextUtil;
import io.github.panxiaochao.gateway.constants.GatewayGlobalConstant;
import io.github.panxiaochao.gateway.constants.OrderConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * {@code AuthFilter}
 * <p> description: 生成唯一编码
 *
 * @author Lypxc
 * @since 2023-02-06
 */
@Component
public class RequestFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOGGER.info(">>> RequestFilter");
        // 生成唯一请求号uuid
        String requestNo = UUID.randomUUID().toString().replaceAll("-", "");
        // 增加请求头中的请求号、Application Name、IP，用于简易白名单服务拦截
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(GatewayGlobalConstant.REQUEST_NO_HEADER_NAME, requestNo)
                .header(GatewayGlobalConstant.APPLICATION_NAME, SpringContextUtil.getApplicationName())
                .header(GatewayGlobalConstant.HOST_IP, LocalhostUtil.getHostIp())
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return OrderConstant.ORDER_REQUEST;
    }
}
