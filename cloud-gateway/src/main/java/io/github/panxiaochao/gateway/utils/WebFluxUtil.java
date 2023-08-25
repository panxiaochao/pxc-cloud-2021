package io.github.panxiaochao.gateway.utils;

import io.github.panxiaochao.core.response.R;
import io.github.panxiaochao.core.utils.JacksonUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * WebFlux 工具类.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-08
 */
public class WebFluxUtil {

	private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

	/**
	 * 判断路径是否与路径模式匹配
	 * @param patterns 路径模式数组
	 * @param path url
	 * @return 是否匹配
	 */
	public static boolean isPathMatch(String[] patterns, String path) {
		return isPathMatch(Arrays.asList(patterns), path);
	}

	/**
	 * 判断路径是否与路径模式匹配
	 * @param patterns 路径模式字符串List
	 * @param path url
	 * @return 是否匹配
	 */
	public static boolean isPathMatch(List<String> patterns, String path) {
		for (String pattern : patterns) {
			if (PATH_MATCHER.match(pattern, path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是GET请求
	 */
	public static boolean isGetRequest(HttpMethod method) {
		return method == HttpMethod.GET;
	}

	/**
	 * 是否是POST请求
	 */
	public static Boolean isPostRequest(HttpMethod method, String contentType) {
		return (method == HttpMethod.POST || method == HttpMethod.PUT)
				&& (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(contentType)
						|| MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType));
	}

	/**
	 * 是否是Json请求
	 * @param contentType 请求头
	 */
	public static boolean isJsonRequest(String contentType) {
		return MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)
				|| MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(contentType);
	}

	/**
	 * 设置webflux模型响应
	 * @param response ServerHttpResponse
	 * @param value 响应内容
	 * @return Mono<Void>
	 */
	public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, Object value) {
		return webFluxResponseWriter(response, HttpStatus.OK, value, HttpStatus.OK.value());
	}

	/**
	 * 设置webflux模型响应
	 * @param response ServerHttpResponse
	 * @param code 响应状态码
	 * @param value 响应内容
	 * @return Mono<Void>
	 */
	public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, Object value, int code) {
		return webFluxResponseWriter(response, HttpStatus.OK, value, code);
	}

	/**
	 * 设置webflux模型响应
	 * @param response ServerHttpResponse
	 * @param status http状态码
	 * @param code 响应状态码
	 * @param value 响应内容
	 * @return Mono<Void>
	 */
	public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, HttpStatus status, Object value,
			int code) {
		return webFluxResponseWriter(response, MediaType.APPLICATION_JSON_VALUE, status, value, code);
	}

	/**
	 * 设置webflux模型响应
	 * @param response ServerHttpResponse
	 * @param contentType content-type
	 * @param status http状态码
	 * @param code 响应状态码
	 * @param value 响应内容
	 * @return Mono<Void>
	 */
	public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType, HttpStatus status,
			Object value, int code) {
		response.setStatusCode(status);
		response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
		String result = JacksonUtil.toString(R.fail(code, value.toString()));
		DataBuffer dataBuffer = response.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
		return response.writeWith(Flux.just(dataBuffer));
	}

	/**
	 * 获取Body数据流转化为字符串
	 * @param serverHttpRequest ServerHttpRequest
	 * @return 字符串数据
	 */
	public static String getRequestBody(ServerHttpRequest serverHttpRequest) {
		Flux<DataBuffer> body = serverHttpRequest.getBody();
		AtomicReference<String> bodyReference = new AtomicReference<>();
		body.subscribe(buffer -> {
			CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
			DataBufferUtils.release(buffer);
			bodyReference.set(charBuffer.toString());
		});
		return bodyReference.get();
	}

}
