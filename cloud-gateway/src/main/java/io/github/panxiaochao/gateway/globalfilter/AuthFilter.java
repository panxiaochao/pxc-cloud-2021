package io.github.panxiaochao.gateway.globalfilter;

import io.github.panxiaochao.gateway.constants.FilterOrderConstant;
import io.github.panxiaochao.gateway.constants.GatewayGlobalConstant;
import io.github.panxiaochao.gateway.properties.GatewayCommonProperties;
import io.github.panxiaochao.gateway.utils.WebFluxUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 权限拦截.
 * </p>
 * <p>
 * 此处不检验token是否合法，放权给各个微服务，确保网关无业务代码，不积压，提升性能.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-06
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

	@Resource
	private GatewayCommonProperties gatewayCommonProperties;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		LOGGER.info(">>> AuthFilter");
		ServerHttpRequest request = exchange.getRequest();

		// 1.跳过白名单
		String url = request.getURI().getPath();
		List<String> whiteList = gatewayCommonProperties.getWhiteList();
		if (WebFluxUtil.isPathMatch(whiteList, url)) {
			return chain.filter(exchange);
		}

		// 2.自定义Header拦截，拦截到X-REQUEST-NO有值的话，说明是内部请求，此请求不合法
		String xRequestNo = getToken(request, GatewayGlobalConstant.X_REQUEST_NO);
		if (StringUtils.isNotBlank(xRequestNo)) {
			return unauthorizedResponse(exchange, "Illegal request to disable access [request url is inner]");
		}

		// 3.获取Token
		String token = getToken(request, GatewayGlobalConstant.ACCESS_TOKEN_HEADER_NAME);
		if (StringUtils.isBlank(token)) {
			return unauthorizedResponse(exchange,
					"Illegal request to disable access [Authorization token is required]");
		}

		return chain.filter(exchange);
	}

	/**
	 * 获取请求token
	 */
	private String getToken(ServerHttpRequest request, String headerName) {
		String token = request.getHeaders().getFirst(headerName);
		// 如果前端设置了令牌前缀，则裁剪掉前缀
		if (StringUtils.isNotBlank(token) && token.startsWith(GatewayGlobalConstant.ACCESS_TOKEN_PREFIX)) {
			token = token.replaceFirst(GatewayGlobalConstant.ACCESS_TOKEN_PREFIX, StringUtils.EMPTY);
		}
		return token;
	}

	/**
	 * 统一异常输出
	 */
	private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
		return WebFluxUtil.webFluxResponseWriter(exchange.getResponse(), msg, HttpStatus.UNAUTHORIZED.value());
	}

	@Override
	public int getOrder() {
		return FilterOrderConstant.ORDER_AUTH;
	}

}
