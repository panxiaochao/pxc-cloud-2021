package io.github.panxiaochao.gateway.globalfilter;

import io.github.panxiaochao.gateway.constants.FilterOrderConstant;
import io.github.panxiaochao.gateway.utils.SqlInjectionUtil;
import io.github.panxiaochao.gateway.utils.WebFluxUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * <p>
 * SQL注入拦截器
 * </p>
 *
 * @author Lypxc
 * @since 2023-08-24
 */
@Component
public class SqlInjectionFilter implements GlobalFilter, Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(SqlInjectionFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// LOGGER.info(">>> SqlInjectionFilter");
		ServerHttpRequest request = exchange.getRequest();
		HttpMethod method = request.getMethod();
		String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

		if (WebFluxUtil.isGetRequest(method)) {
			String rawQuery = request.getURI().getRawQuery();
			if (StringUtils.isBlank(rawQuery)) {
				return chain.filter(exchange);
			}

			// 执行sql注入清理
			boolean isSqlInjection = SqlInjectionUtil.checkForGet(rawQuery);

			// 如果存在sql注入,直接拦截请求
			if (isSqlInjection) {
				return WebFluxUtil.webFluxResponseWriter(exchange.getResponse(),
						"The Request URI Have a SqlInjection Error", HttpStatus.UNAUTHORIZED.value());
			}

		}
		// TODO POST 还需要加入
		// 不对参数做任何处理
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return FilterOrderConstant.ORDER_SQL_INJECTION;
	}

}
