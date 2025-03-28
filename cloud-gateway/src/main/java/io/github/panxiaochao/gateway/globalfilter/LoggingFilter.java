package io.github.panxiaochao.gateway.globalfilter;

import io.github.panxiaochao.core.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *
 * <p>
 * 网关日志记录拦截
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-06
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

	private static final String START_TIME = "startTime";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		LOGGER.info(">>> LoggingFilter");
		String scheme = exchange.getRequest().getURI().getScheme();
		String rawPath = exchange.getRequest().getURI().getRawPath();
		HttpMethod httpMethod = exchange.getRequest().getMethod();
		MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
		String host = exchange.getRequest().getURI().getHost();
		int port = exchange.getRequest().getURI().getPort();
		String basePath = scheme + "://" + host + ":" + port + rawPath;
		exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			Long startTime = exchange.getAttribute(START_TIME);
			if (startTime != null) {
				long executeTime = (System.currentTimeMillis() - startTime);
				String ip = IpUtil.getHostIp();
				HttpStatus httpStatus = exchange.getResponse().getStatusCode();
				int code = httpStatus != null ? httpStatus.value() : 500;
				LOGGER.info("Ip：{}, Path：{}, HttpMethod: {}, Query: {}, StatusCode：{}, Cost：{} ms", ip, basePath,
						httpMethod, queryParams, code, executeTime);
			}
		}));
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
