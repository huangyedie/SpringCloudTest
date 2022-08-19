package provider.controllers;


import brave.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/providerTest")
public class TestController {


    @Value("${server.port}")
    String port;
    @Resource
    private Tracer tracer;

    @GetMapping("/hi")
    public String hi(@RequestParam(value = "name", defaultValue = "forezp", required = false) String name) {

        //获取链路id
        System.out.println("TrackId" + tracer.currentSpan().context().traceIdString());
        System.out.println("SpanId" + tracer.currentSpan().context().spanIdString());
        return "providerHello " + name + ", i'm provider ,my port:" + port;
    }

    @GetMapping("/testGateWay")
    public String TestGateWa() {
        return "provider testGateWay, i'm provider ,my port:" + port;
    }
}
