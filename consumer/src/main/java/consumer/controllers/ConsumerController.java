package consumer.controllers;


import brave.Tracer;
import brave.propagation.TraceContext;
import consumer.FeignClient.ProviderClient;

import feign.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/consumerTest")
public class ConsumerController {

    @Resource
    private ProviderClient providerClient;
    private static Logger log = LoggerFactory.getLogger(ConsumerController.class);
    @Value("${server.port}")
    String port;

    @Resource
    private Tracer tracer;

    @GetMapping("/hi-xiaoFeiFeign")
    public String Test() {

        //获取链路id
        TraceContext a = tracer.currentSpan().context();
        System.out.println("TrackId" + tracer.currentSpan().context().traceIdString());
        System.out.println("SpanId" + tracer.currentSpan().context().spanIdString());
        //log.info("1成为大师傅士大夫");
        return providerClient.hi("Test");
    }

    @GetMapping("/testGateWay")
    public String TestGateWa() {
        return "consumer testGateWay, i'm provider ,my port:" + port;
    }
}
