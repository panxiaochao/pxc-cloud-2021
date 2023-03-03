package io.github.panxiaochao.gateway.constants;

/**
 * {@code OrderConstants}
 * <p> description: 全局常量
 *
 * @author Lypxc
 * @since 2023-02-09
 */
public interface GatewayGlobalConstant {

    /**
     * 请求唯一编码 请求头HeaderName
     */
    String REQUEST_NO_HEADER_NAME = "REQUEST-NO";

    /**
     * APPLICATION_NAME
     */
    String APPLICATION_NAME = "APPLICATION-NAME";

    /**
     * HOST-IP
     */
    String HOST_IP = "HOST-IP";

    /**
     * TOKEN HEADER
     */
    String ACCESS_TOKEN_HEADER_NAME = "Authorization";
    String ACCESS_TOKEN_PREFIX = "Bearer ";
}
