// package io.github.panxiaochao.gateway.listener;
//
// import com.alibaba.cloud.nacos.NacosConfigManager;
// import com.alibaba.nacos.api.config.ConfigService;
// import com.alibaba.nacos.api.config.listener.Listener;
// import com.alibaba.nacos.api.exception.NacosException;
// import com.alibaba.nacos.common.utils.JacksonUtils;
// import com.fasterxml.jackson.core.type.TypeReference;
// import org.apache.commons.lang3.StringUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
// import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
// import org.springframework.cloud.gateway.route.RouteDefinition;
// import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
// import org.springframework.context.ApplicationEventPublisher;
// import org.springframework.context.annotation.Configuration;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
// import reactor.core.scheduler.Schedulers;
//
// import javax.annotation.PostConstruct;
// import javax.annotation.Resource;
// import java.util.List;
// import java.util.concurrent.Executor;
// import java.util.stream.Collectors;
//
// /**
// * {@code NacosListener}
// * <p> description: Nacos动态监听
// *
// * @author Lypxc
// * @since 2023-03-01
// */
// @Configuration
// public class NacosListener {
//
// @Resource
// private NacosConfigManager nacosConfigManager;
//
// @Resource
// private RouteDefinitionWriter routeDefinitionWriter;
//
// @Resource
// private InMemoryRouteDefinitionRepository routeDefinitionLocator;
//
// @Resource
// private ApplicationEventPublisher applicationEventPublisher;
//
// private static final Logger log = LoggerFactory.getLogger(NacosListener.class);
//
// private static final String DATA_ID = "pxc-dev";
//
// private static final String GROUP_ID = "pxc-dev";
//
//
// @PostConstruct
// public void init() throws NacosException {
// nacosConfigListener();
// }
//
// public void nacosConfigListener() throws NacosException {
// log.info("Spring Gateway 开始读取 Nacos 路由配置");
//
// ConfigService configService = nacosConfigManager.getConfigService();
//
// if(configService == null){
// throw new RuntimeException("Spring Gateway Nacos 动态路由启动失败");
// }
//
// String configInfo = configService.getConfig(DATA_ID, GROUP_ID, 100000);
//
// List<RouteDefinition> definitionList = JacksonUtils.toObj(configInfo, new
// TypeReference<List<RouteDefinition>>() {});
//
// for(RouteDefinition definition : definitionList){
// log.info("Spring Gateway Nacos 路由配置 : {}", definition.toString());
// routeDefinitionWriter.save(Mono.just(definition)).block();
// }
//
// configService.addListener(DATA_ID, GROUP_ID, new Listener() {
// @Override
// public Executor getExecutor() {
// return null;
// }
//
// @Override
// public void receiveConfigInfo(String configInfo) {
// if (StringUtils.isNotBlank(configInfo)){
// // 序列化新路由
// List<RouteDefinition> updateDefinitionList = JacksonUtils.toObj(configInfo, new
// TypeReference<List<RouteDefinition>>() {});
//
// // 拿到新路由的所有id
// List<String> ids =
// updateDefinitionList.stream().map(RouteDefinition::getId).collect(Collectors.toList());
//
// // 拿到旧路由数据
// Flux<RouteDefinition> routeDefinitions = routeDefinitionLocator.getRouteDefinitions();
//
// routeDefinitions.doOnNext(r -> {
// String id = r.getId();
// if (!ids.contains(id)) {
// routeDefinitionWriter.delete(Mono.just(id)).subscribeOn(Schedulers.parallel()).subscribe();
// log.info("Spring Gateway 删除 Nacos 路由配置 : {}", id);
// }
// }).doOnComplete(() -> {
//
// }).doAfterTerminate(() -> {
// for(RouteDefinition definition : updateDefinitionList){
// log.info("Spring Gateway merge Nacos 路由配置 : {}", definition.toString());
// routeDefinitionWriter.save(Mono.just(definition)).subscribeOn(Schedulers.parallel()).subscribe();
// }
// }).subscribe();
//
// applicationEventPublisher.publishEvent(new RefreshRoutesEvent(new Object()));
// } else {
// log.info("当前网关无动态路由相关配置");
// }
// }
// });
// }
// }
