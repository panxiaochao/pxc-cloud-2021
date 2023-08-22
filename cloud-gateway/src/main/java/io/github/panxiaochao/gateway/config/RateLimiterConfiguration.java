package io.github.panxiaochao.gateway.config;

import io.github.panxiaochao.gateway.constants.GatewayGlobalConstant;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * <p>
 * 路由流量配置.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-09
 */
@Configuration
public class RateLimiterConfiguration {

	/**
	 * IP 限流
	 * @return 限流key
	 */
	@Bean
	@Primary
	public KeyResolver ipKeyResolver() {
		return exchange -> Mono
			.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
	}

	/**
	 * 用户限流, 通过当前Token
	 * @return 限流key
	 */
	@Bean
	public KeyResolver userKeyResolver() {
		// 使用这种方式限流，请求Header中必须携带X-Access-Token参数
		return exchange -> Mono.just(Objects.requireNonNull(
				exchange.getRequest().getHeaders().getFirst(GatewayGlobalConstant.ACCESS_TOKEN_HEADER_NAME)));
	}

	/**
	 * 请求路径限流
	 * @return 限流key
	 */
	@Bean
	public KeyResolver apiKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getPath().value());
	}

}
