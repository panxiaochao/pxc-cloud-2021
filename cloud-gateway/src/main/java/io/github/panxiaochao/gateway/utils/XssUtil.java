package io.github.panxiaochao.gateway.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * <p>
 * Xss工具类.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-09
 */
public class XssUtil {

	private XssUtil() {
	}

	private static final Pattern[] PATTERNS = {
			// Avoid anything in a <script> type of expression
			Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
			// Avoid anything in a src='...' type of expression
			Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// Remove any lonesome </script> tag
			Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
			// Avoid anything in a <iframe> type of expression
			Pattern.compile("<iframe>(.*?)</iframe>", Pattern.CASE_INSENSITIVE),
			// Remove any lonesome <script ...> tag
			Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// Remove any lonesome <img ...> tag
			Pattern.compile("<img(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// Avoid eval(...) expressions
			Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// Avoid expression(...) expressions
			Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// Avoid javascript:... expressions
			Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
			// Avoid vbscript:... expressions
			Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
			// Avoid onload= expressions
			Pattern.compile("on(load|error|mouseover|submit|reset|focus|click)(.*?)=",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL) };

	/**
	 * 清除不合法标签
	 * @param value 字符串
	 * @return 过滤字符
	 */
	public static String cleanXss(String value) {
		if (StringUtils.hasText(value)) {
			value = value.replaceAll("\0|\n|\r", "");
			for (Pattern scriptPattern : PATTERNS) {
				value = scriptPattern.matcher(value).replaceAll("");
			}
			value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		}
		return value;
	}

}
