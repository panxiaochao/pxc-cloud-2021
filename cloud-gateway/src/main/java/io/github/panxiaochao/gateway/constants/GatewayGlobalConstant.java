package io.github.panxiaochao.gateway.constants;

/**
 * <p>
 * 全局常量.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-09
 */
public interface GatewayGlobalConstant {

	/**
	 * 请求唯一编码 请求头HeaderName
	 */
	String X_REQUEST_NO = "X-REQUEST-NO";

	/**
	 * 本机IP，用于内部白名单校验
	 */
	String HOST_IP = "HOST-IP";

	/**
	 * 请求IP，用于内部白名单校验
	 */
	String REQUEST_IP = "REQUEST-IP";

	/**
	 * TOKEN HEADER
	 */
	String ACCESS_TOKEN_HEADER_NAME = "Authorization";

	/**
	 * TOKEN HEADER PREFIX
	 */
	String ACCESS_TOKEN_PREFIX = "Bearer ";

}
