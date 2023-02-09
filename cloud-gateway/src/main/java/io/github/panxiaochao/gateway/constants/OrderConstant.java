package io.github.panxiaochao.gateway.constants;

/**
 * {@code OrderConstant}
 * <p> description: 排序常量，Order值越小，优先级越高
 *
 * @author Lypxc
 * @since 2023-02-06
 */
public interface OrderConstant {

    Integer ORDER_REQUEST_WRAPPER = -40;

    Integer ORDER_REQUEST = -30;

    Integer ORDER_AUTH = -20;

    Integer ORDER_XSS = -10;
}
