package io.github.panxiaochao.gateway.globalfilter;

import io.github.panxiaochao.gateway.constants.FilterOrderConstant;
import io.github.panxiaochao.gateway.properties.GatewayCommonProperties;
import io.github.panxiaochao.gateway.utils.WebFluxUtil;
import io.github.panxiaochao.gateway.utils.XssUtil;
import io.netty.buffer.ByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * Xss拦截.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-06
 */
@Component
public class XssFilter implements GlobalFilter, Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(XssFilter.class);

	@Resource
	private GatewayCommonProperties gatewayCommonProperties;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// LOGGER.info(">>> XssFilter");
		ServerHttpRequest request = exchange.getRequest();
		String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

		// 1.判断Xss是否开启
		if (!gatewayCommonProperties.getXss().getEnabled()) {
			return chain.filter(exchange);
		}
		// 2.GET DELETE 不过滤
		HttpMethod method = request.getMethod();
		if (method == null || method == HttpMethod.GET || method == HttpMethod.DELETE) {
			return chain.filter(exchange);
		}

		// 3.非JSON请求不过滤
		if (!WebFluxUtil.isJsonRequest(contentType)) {
			return chain.filter(exchange);
		}

		// 4.剩下的都过滤
		ServerHttpRequestDecorator httpRequestDecorator = requestDecorator(exchange);
		return chain.filter(exchange.mutate().request(httpRequestDecorator).build());
	}

	private ServerHttpRequestDecorator requestDecorator(ServerWebExchange exchange) {
		return new ServerHttpRequestDecorator(exchange.getRequest()) {
			@Override
			public Flux<DataBuffer> getBody() {
				Flux<DataBuffer> body = super.getBody();
				return body.buffer().map(dataBuffers -> {
					DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
					DataBuffer join = dataBufferFactory.join(dataBuffers);
					byte[] content = new byte[join.readableByteCount()];
					join.read(content);
					DataBufferUtils.release(join);
					String bodyStr = new String(content, StandardCharsets.UTF_8);
					// 防xss攻击过滤
					bodyStr = XssUtil.cleanXss(bodyStr);
					// 转成字节
					byte[] bytes = bodyStr.getBytes(StandardCharsets.UTF_8);
					NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(
							ByteBufAllocator.DEFAULT);
					DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
					buffer.write(bytes);
					return buffer;
				});
			}

			@Override
			public HttpHeaders getHeaders() {
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.putAll(super.getHeaders());
				// 由于修改了请求体的body，导致content-length长度不确定，因此需要删除原先的content-length
				// 长度是字节长度，不是字符串长度
				httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
				httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
				return httpHeaders;
			}

		};
	}

	@Override
	public int getOrder() {
		return FilterOrderConstant.ORDER_XSS;
	}

}
