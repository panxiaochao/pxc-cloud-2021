package io.github.panxiaochao.gateway.globalfilter;

import io.github.panxiaochao.core.utils.UuidUtil;
import io.github.panxiaochao.gateway.constants.FilterOrderConstant;
import io.github.panxiaochao.gateway.constants.GatewayGlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * <p>
 * 拦截请求, 生成唯一编码.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-06
 */
@Component
public class RequestFilter implements GlobalFilter, Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// LOGGER.info(">>> RequestFilter");
		ServerHttpRequest request = exchange.getRequest();
		// 增加请求头中的请求号、IP，用于简易白名单服务拦截
		ServerHttpRequest newRequest = request.mutate()
			.header(GatewayGlobalConstant.X_REQUEST_NO, UuidUtil.getSimpleUUID())
			.header(GatewayGlobalConstant.REQUEST_IP,
					Objects.requireNonNull(request.getRemoteAddress()).getHostString())
			.build();
		return chain.filter(exchange.mutate().request(newRequest).build());
	}

	@Override
	public int getOrder() {
		return FilterOrderConstant.ORDER_REQUEST;
	}

}
