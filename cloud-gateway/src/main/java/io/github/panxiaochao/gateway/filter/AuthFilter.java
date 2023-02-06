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
 * {@code AuthFilter}
 * <p> description:
 *
 * @author Lypxc
 * @since 2023-02-06
 */
public class AuthFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOGGER.info(">>> AuthFilter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return OrderConstants.ORDER_AUTH;
    }
}
