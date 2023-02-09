package io.github.panxiaochao.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code GatewaySpringDocConfig}
 * <p> description: SpringDoc配置
 *
 * @author Lypxc
 * @since 2023-02-09
 */
public class GatewaySpringDocConfig {

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters, RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        assert definitions != null;
        definitions.stream().filter(routeDefinition -> !routeDefinition.getId().equals("openapi")).forEach(routeDefinition -> {
            if (routeDefinition.getId().startsWith("ReactiveCompositeDiscoveryClient")) {
                return;
            }
            String name = routeDefinition.getPredicates().get(0).getArgs().values().stream().findFirst().get();
            name = name.replace("/**", "").replace("/", "");
            swaggerUiConfigParameters.addGroup(name);
            GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(routeDefinition.getId()).build();
        });
        return groups;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("MakuCloud")
                        .description("cloud-gateway")
                        .contact(new Contact().name("Lypxc"))
                        .version("1.0")
                        .termsOfService("https://www.baidu.com")
                        .license(new License().name("MIT")
                                .url("https://maku.net")));
    }
}
