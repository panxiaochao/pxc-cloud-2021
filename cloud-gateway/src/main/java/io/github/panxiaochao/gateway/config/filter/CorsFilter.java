package io.github.panxiaochao.gateway.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 跨域拦截器.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-06
 */
public class CorsFilter implements WebFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);

	/**
	 * 当前跨域请求最大有效时长，同一个域名不会再进行检查，默认3600
	 */
	private static final String MAX_AGE = "1800L";

	/**
	 * ALL
	 */
	private static final String ALL = "*";

	/**
	 * 允许请求的方法
	 */
	private static final List<String> ALLOW_METHODS = Arrays.asList("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE",
			"PATCH");

	private static final String ALLOW_HEADERS = "Authorization, Origin, X-Requested-With, Content-Type, Accept";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		LOGGER.info(">>> CorsFilter");
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		// 请求路径: /favicon.ico 404 NOT_FOUND
		if ("/favicon.ico".equals(request.getURI().getPath())) {
			response.setStatusCode(HttpStatus.OK);
			return Mono.empty();
		}
		if (CorsUtils.isCorsRequest(request)) {
			HttpHeaders headers = response.getHeaders();
			headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALL);
			// headers.add("Access-Control-Allow-Origin-Patterns", "*");
			headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALLOW_HEADERS);
			headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(",", ALLOW_METHODS));
			headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.toString(true));
			headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
			headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
			if (request.getMethod() == HttpMethod.OPTIONS) {
				response.setStatusCode(HttpStatus.OK);
				return Mono.empty();
			}
		}
		return chain.filter(exchange);
	}

}
