package provider.controllers;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/providerTest")
public class TestController {


    @Value("${server.port}")
    String port;

    @GetMapping("/hi")
    public String hi(@RequestParam(value = "name", defaultValue = "forezp", required = false) String name) {
        return "providerHello " + name + ", i'm provider ,my port:" + port;
    }

    @GetMapping("/testGateWay")
    public String TestGateWa() {
        return "provider testGateWay, i'm provider ,my port:" + port;
    }
}
