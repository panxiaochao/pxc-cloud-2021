package io.github.panxiaochao.gateway.constants;

/**
 * <p>
 * 排序常量，Order值越小，优先级越高.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-06
 */
public interface FilterOrderConstant {

	/**
	 * 重载请求Body数据
	 */
	Integer ORDER_REQUEST_WRAPPER = -10;

	/**
	 * XSS拦截
	 */
	Integer ORDER_XSS = 0;

	/**
	 * SQL_INJECTION注入拦截
	 */
	Integer ORDER_SQL_INJECTION = 5;

	/**
	 * 权限拦截
	 */
	Integer ORDER_AUTH = 10;

	/**
	 * 请求Head放入请求唯一值
	 */
	Integer ORDER_REQUEST = 20;

}
