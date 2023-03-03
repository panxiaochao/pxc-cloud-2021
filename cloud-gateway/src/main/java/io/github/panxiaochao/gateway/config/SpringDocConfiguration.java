package io.github.panxiaochao.gateway.config;

import io.github.panxiaochao.common.utils.SpringContextUtil;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * {@code SpringDocConfiguration}
 * <p> description: SpringDoc 聚合微服务配置
 *
 * @author Lypxc
 * @since 2023-02-09
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SpringDocConfiguration {

    private static final String DISCOVERY_CLIENT_ID_PRE = "ReactiveCompositeDiscoveryClient_";

    private static final String V3_API_DOCS = "/%s/v3/api-docs";

    private final SwaggerUiConfigProperties swaggerUiConfigProperties;

    private final RouteDefinitionLocator locator;

    @PostConstruct
    public void apis() {
        final String selfServiceName = SpringContextUtil.getApplicationName();
        final String selfServiceId = DISCOVERY_CLIENT_ID_PRE + selfServiceName;
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        if (!CollectionUtils.isEmpty(definitions)) {
            // 解析出所有子服务名
            definitions.stream()
                    .filter(routeDefinition -> Optional.ofNullable(routeDefinition.getId()).isPresent())
                    // 只保留服务级别的路由。
                    .filter(routeDefinition -> routeDefinition.getId().startsWith(DISCOVERY_CLIENT_ID_PRE))
                    // 排除本系统。
                    .filter(routeDefinition -> !selfServiceId.equalsIgnoreCase(routeDefinition.getId()))
                    .forEach(routeDefinition -> {
                        String serverName = routeDefinition.getId().replace(DISCOVERY_CLIENT_ID_PRE, "").toLowerCase();
                        AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                                serverName,
                                String.format(V3_API_DOCS, serverName),
                                serverName
                        );
                        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = swaggerUiConfigProperties.getUrls();
                        if (urls == null) {
                            urls = new LinkedHashSet<>();
                            swaggerUiConfigProperties.setUrls(urls);
                        }
                        urls.add(swaggerUrl);
                    });
        }
    }
}
