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
     * TOKEN HEADER
     */
    String ACCESS_TOKEN_HEADER_NAME = "Authorization";
    String ACCESS_TOKEN_PREFIX = "Bearer ";
}
