//package gateway.config;
//
//import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
//import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
//import com.alibaba.csp.sentinel.slots.block.RuleConstant;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.codec.ServerCodecConfigurer;
//import org.springframework.web.reactive.result.view.ViewResolver;
//
//import javax.annotation.PostConstruct;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Configuration
//public class GatewayConfiguration {
//
//    private final List<ViewResolver> viewResolvers;
//    private final ServerCodecConfigurer serverCodecConfigurer;
//
//    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
//                                ServerCodecConfigurer serverCodecConfigurer) {
//        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
//        this.serverCodecConfigurer = serverCodecConfigurer;
//    }
//
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
//        // Register the block exception handler for Spring Cloud Gateway.
//        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
//    }
//
//    @Bean
//    @Order(-1)
//    public GlobalFilter sentinelGatewayFilter() {
//        return new SentinelGatewayFilter();
//    }
//
////    @PostConstruct
////    public void doInit() {
////        initCustomizedApis();
////        initGatewayRules();
////    }
////
////    private void initCustomizedApis() {
////        Set<ApiDefinition> definitions = new HashSet<>();
////        ApiDefinition api1 = new ApiDefinition("consumer")
////                .setPredicateItems(new HashSet<ApiPredicateItem>() );
////        ApiDefinition api2 = new ApiDefinition("provider")
////                .setPredicateItems(new HashSet<ApiPredicateItem>() );
////        definitions.add(api1);
////        definitions.add(api2);
////        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
////    }
////
////    private void initGatewayRules() {
////        Set<GatewayFlowRule> rules = new HashSet<>();
////        rules.add(new GatewayFlowRule("consumer")
////                .setCount(10)
////                .setIntervalSec(1)
////        );
////        rules.add(new GatewayFlowRule("consumer")
////                .setCount(2)
////                .setIntervalSec(2)
////                .setBurst(2)
////                .setParamItem(new GatewayParamFlowItem()
////                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
////                )
////        );
////        rules.add(new GatewayFlowRule("provider")
////                .setCount(10)
////                .setIntervalSec(1)
////                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
////                .setMaxQueueingTimeoutMs(600)
////                .setParamItem(new GatewayParamFlowItem()
////                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER)
////                        .setFieldName("X-Sentinel-Flag")
////                )
////        );
////        rules.add(new GatewayFlowRule("provider")
////                .setCount(1)
////                .setIntervalSec(1)
////                .setParamItem(new GatewayParamFlowItem()
////                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
////                        .setFieldName("pa")
////                )
////        );
////        rules.add(new GatewayFlowRule("provider")
////                .setCount(2)
////                .setIntervalSec(30)
////                .setParamItem(new GatewayParamFlowItem()
////                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
////                        .setFieldName("type")
////                        .setPattern("warn")
////                        .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_CONTAINS)
////                )
////        );
////
////        rules.add(new GatewayFlowRule("provider")
////                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
////                .setCount(5)
////                .setIntervalSec(1)
////                .setParamItem(new GatewayParamFlowItem()
////                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
////                        .setFieldName("pn")
////                )
////        );
////        GatewayRuleManager.loadRules(rules);
////    }
//
//}