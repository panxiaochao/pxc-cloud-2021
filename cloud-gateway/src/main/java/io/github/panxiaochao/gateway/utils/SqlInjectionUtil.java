package io.github.panxiaochao.gateway.utils;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.panxiaochao.core.utils.JacksonUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * <p>
 * SQL注入处理工具类
 * </p>
 *
 * @author Lypxc
 * @since 2023-08-24
 */
public class SqlInjectionUtil {

	private static final Logger log = LoggerFactory.getLogger(SqlInjectionUtil.class);

	private static final String SQL_REGEX = "\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";

	private static final Pattern SQL_PATTERN = Pattern.compile(SQL_REGEX, Pattern.CASE_INSENSITIVE);

	private SqlInjectionUtil() {
	}

	private static boolean match(String lowerValue, String param) {
		if (SQL_PATTERN.matcher(param).find()) {
			log.error("The parameter contains keywords {} that do not allow SQL!", lowerValue);
			return true;
		}
		return false;
	}

	private static boolean checking(Object value) {
		String lowerValue = Objects.isNull(value) ? "" : value.toString().toLowerCase();
		return SQL_PATTERN.matcher(lowerValue).find();
	}

	/**
	 * get请求sql注入校验
	 * @param value 具体的检验
	 * @return 是否存在不合规内容
	 */
	public static boolean checkForGet(String value) {

		// 参数需要url编码
		// 这里需要将参数转换为小写来处理
		// 不改变原值
		// value示例 order=asc&pageNum=1&pageSize=100&parentId=0
		String lowerValue;
		try {
			lowerValue = URLDecoder.decode(value, StandardCharsets.UTF_8.name()).toLowerCase();
		}
		catch (UnsupportedEncodingException e) {
			// skip
			return false;
		}

		// 获取到请求中所有参数值-取每个key=value组合第一个等号后面的值
		return Stream.of(lowerValue.split("\\&"))
			.map(kp -> kp.substring(kp.indexOf("=") + 1))
			.parallel()
			.anyMatch(param -> match(lowerValue, param));
	}

	/**
	 * post请求sql注入校验
	 * @param value 具体的检验
	 * @return 是否存在不合规内容
	 */
	public static boolean checkForPost(String value) {
		List<JsonNode> result = new ArrayList<>();
		JsonNode jsonNode = JacksonUtil.transferToJsonNode(value);
		iterator(jsonNode, result);
		return CollectionUtils.isNotEmpty(result);
	}

	private static void iterator(JsonNode jsonNode, List<JsonNode> result) {
		if (jsonNode.isEmpty()) {
			return;
		}

		// 非JSON结构
		if (jsonNode.isValueNode()) {
			boolean hasInjection = checking(jsonNode.asText());
			if (hasInjection) {
				result.add(jsonNode);
			}
			return;
		}

		// 数组结构
		if (jsonNode.isArray()) {
			if (ObjectUtils.isNotEmpty(jsonNode)) {
				for (JsonNode je : jsonNode) {
					iterator(je, result);
				}
			}
			return;
		}

		// 对象结构
		if (jsonNode.isObject()) {
			if (ObjectUtils.isNotEmpty(jsonNode)) {
				Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
				while (fields.hasNext()) {
					Map.Entry<String, JsonNode> jsonField = fields.next();
					iterator(jsonField.getValue(), result);
				}
			}
		}
	}

}
