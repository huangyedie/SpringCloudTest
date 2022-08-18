package consumer.controllers;

import consumer.FeignClient.ProviderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/hi-xiaoFeiFeign")
    public String Test() {
        //log.info("1成为大师傅士大夫");
        return providerClient.hi("Test");
    }

    @GetMapping("/testGateWay")
    public String TestGateWa() {
        return "consumer testGateWay, i'm provider ,my port:" + port;
    }
}
