package io.github.panxiaochao.gateway.constants;

/**
 * <p>
 * 排序常量，Order值越小，优先级越高.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-06
 */
public interface OrderConstant {

	/**
	 * 重载请求Body数据
	 */
	Integer ORDER_REQUEST_WRAPPER = -40;

	/**
	 * 请求Head放入请求唯一值
	 */
	Integer ORDER_REQUEST = -30;

	/**
	 * 权限拦截
	 */
	Integer ORDER_AUTH = -20;

	/**
	 * XSS拦截
	 */
	Integer ORDER_XSS = -10;

}
