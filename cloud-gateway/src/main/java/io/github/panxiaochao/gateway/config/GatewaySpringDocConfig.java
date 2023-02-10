package io.github.panxiaochao.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code GatewaySpringDocConfig}
 * <p> description: SpringDoc配置
 *
 * @author Lypxc
 * @since 2023-02-09
 */
@Configuration
public class GatewaySpringDocConfig {

    private static final String DISCOVERY_CLIENT_ID_PRE = "ReactiveCompositeDiscoveryClient_";

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters, RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        if (!CollectionUtils.isEmpty(definitions)) {
            definitions.stream()
                    .filter(routeDefinition -> !routeDefinition.getId().equals("openapi"))
                    .forEach(routeDefinition -> {
                        // TODO 这里代码不对
                        if (routeDefinition.getId().startsWith(DISCOVERY_CLIENT_ID_PRE)) {
                            return;
                        }
                        String name = routeDefinition.getPredicates().get(0).getArgs().values().stream().findFirst().get();
                        name = name.replace("/**", "").replace("/", "");
                        swaggerUiConfigParameters.addGroup(name);
                        GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(routeDefinition.getId()).build();
                    });
        }
        return groups;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Spring Cloud Gateway - SpringDoc Api")
                .description("SpringDoc Gateway Api")
                .contact(new Contact().name("Lypxc"))
                .version("1.0"));
    }
}
