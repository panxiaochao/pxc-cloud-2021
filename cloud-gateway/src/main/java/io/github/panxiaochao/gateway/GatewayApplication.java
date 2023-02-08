package io.github.panxiaochao.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;

/**
 * {@code GatewayApplication}
 * <p> description: 启动器
 *
 * @author Lypxc
 * @since 2023-02-06
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(value = {"io.github.panxiaochao"})
@EnableDiscoveryClient
public class GatewayApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayApplication.class);

    private static final String CONTEXT_PATH = "/";

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext application = SpringApplication.run(GatewayApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String applicationName = env.getProperty("spring.application.name");
        String port = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (!StringUtils.hasText(contextPath) || CONTEXT_PATH.equals(contextPath)) {
            contextPath = "";
        }
        LOGGER.info("\n----------------------------------------------------------\n\t{}{}{}{}",
                applicationName + " is running! Access URLs:",
                "\n\tLocal    访问网址: \thttp://localhost:" + port + contextPath,
                "\n\tExternal 访问网址: \thttp://" + ip + ":" + port + contextPath,
                "\n----------------------------------------------------------\n");
    }
}
