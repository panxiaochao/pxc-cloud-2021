package io.github.panxiaochao.gateway.config;

import io.github.panxiaochao.gateway.config.filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 网关配置.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-06
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfiguration {

	/**
	 * 跨域拦截
	 * @return CorsFilter
	 */
	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter();
	}

}
