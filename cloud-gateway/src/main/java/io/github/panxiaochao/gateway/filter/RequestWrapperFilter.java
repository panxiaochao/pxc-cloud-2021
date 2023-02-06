package io.github.panxiaochao.gateway.filter;

import io.github.panxiaochao.gateway.constant.OrderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * {@code RequestWrapperFilter}
 * <p> description: 重新包装Body数据，解决是让其输入流可重复读，为后续的GlobalFilter重写post|put请求的body做准备
 *
 * @author Lypxc
 * @since 2023-02-06
 */
public class RequestWrapperFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestWrapperFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOGGER.info(">>> RequestWrapperFilter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return OrderConstants.ORDER_REQUEST_WRAPPER;
    }
}
