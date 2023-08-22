package io.github.panxiaochao.gateway.globalfilter;

import io.github.panxiaochao.gateway.constants.OrderConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * <p>
 * 权限拦截.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-06
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		LOGGER.info(">>> AuthFilter");
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return OrderConstant.ORDER_AUTH;
	}

}
