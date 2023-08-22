// package io.github.panxiaochao.gateway.listener.nacos;
//
// import com.alibaba.nacos.api.naming.listener.NamingEvent;
// import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
// import com.alibaba.nacos.common.notify.Event;
// import com.alibaba.nacos.common.notify.NotifyCenter;
// import com.alibaba.nacos.common.notify.listener.Subscriber;
// import io.github.panxiaochao.common.utils.JacksonUtil;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.cache.Cache;
// import org.springframework.cache.CacheManager;
// import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier;
// import org.springframework.stereotype.Component;
//
// import javax.annotation.PostConstruct;
// import javax.annotation.Resource;
//
//
// /**
// * {@code InstancesChangeEventListener}
// * <p> description: Nacos 实现监听动态路由
// *
// * @author Lypxc
// * @since 2023-03-01
// */
// @Component
// public class InstancesChangeEventListener extends Subscriber<InstancesChangeEvent> {
//
// private static final Logger LOGGER =
// LoggerFactory.getLogger(InstancesChangeEventListener.class);
//
// @Resource
// private CacheManager defaultLoadBalancerCacheManager;
//
// @PostConstruct
// private void post() {
// NotifyCenter.registerSubscriber(this);
// }
//
// @Override
// public void onEvent(InstancesChangeEvent event) {
// NamingEvent namingEvent = new NamingEvent(event.getServiceName(), event.getGroupName(),
// event.getClusters(), event.getHosts());
// LOGGER.info("Spring Cloud Gateway 接收实例刷新事件：{}, 开始刷新缓存", JacksonUtil.toString(event));
// Cache cache =
// defaultLoadBalancerCacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME);
// if (cache != null) {
// cache.evict(event.getServiceName());
// }
// LOGGER.info("Spring Cloud Gateway 实例刷新完成");
//
//
// }
//
// @Override
// public Class<? extends Event> subscribeType() {
// return InstancesChangeEvent.class;
// }
// }
