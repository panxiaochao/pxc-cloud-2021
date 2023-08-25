package io.github.panxiaochao.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 自定义属性配置
 * </p>
 *
 * @author Lypxc
 * @since 2023-08-23
 */
@Getter
@Setter
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = GatewayCommonProperties.GATEWAY_PREFIX, ignoreInvalidFields = true)
public class GatewayCommonProperties {

	/**
	 * 属性前缀
	 */
	public static final String GATEWAY_PREFIX = "pxc.gateway";

	/**
	 * 放行白名单配置，网关不校验此处的白名单
	 */
	private List<String> whiteList = new ArrayList<>();

	/**
	 * Xss 是否开启
	 */
	private Xss xss = new Xss();

	@Getter
	@Setter
	public static class Xss {

		/**
		 * 是否开启
		 */
		private Boolean enabled = false;

	}

}
