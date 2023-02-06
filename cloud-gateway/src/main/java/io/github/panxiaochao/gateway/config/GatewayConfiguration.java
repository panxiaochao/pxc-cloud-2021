package io.github.panxiaochao.gateway.config;

import io.github.panxiaochao.gateway.config.filter.CorsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code GatewayConfiguration}
 * <p> description: 网关配置
 *
 * @author Lypxc
 * @since 2023-02-06
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayConfiguration.class);

    /**
     * 跨域拦截
     *
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }
}
