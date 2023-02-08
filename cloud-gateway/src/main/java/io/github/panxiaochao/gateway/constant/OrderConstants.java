package io.github.panxiaochao.gateway.constant;

/**
 * {@code OrderConstants}
 * <p> description: 排序常量，Order值越小，优先级越高
 *
 * @author Lypxc
 * @since 2023-02-06
 */
public interface OrderConstants {

    Integer ORDER_REQUEST_WRAPPER = 10;

    Integer ORDER_REQUEST = 20;

    Integer ORDER_AUTH = 30;

    Integer ORDER_XSS = 40;
}
