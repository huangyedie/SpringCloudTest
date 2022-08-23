package consumer.controllers;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import consumer.entity.TestEntity;
import consumer.feignClient.ProviderClient;

import consumer.mapper.TestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


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
    @Resource
    private TestMapper testMapper;

    @GetMapping("/hi-xiaoFeiFeign")
    public String Test() {

        try {
            //获取链路id
            TraceContext a = tracer.currentSpan().context();
            System.out.println("TrackId" + tracer.currentSpan().context().traceIdString());
            System.out.println("SpanId" + tracer.currentSpan().context().spanIdString());
            //log.info("1成为大师傅士大夫");
            return providerClient.hi("Test");
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @GetMapping("/testGateWay")
    public String TestGateWa() {
        return "consumer testGateWay, i'm provider ,my port:" + port;
    }

    @GetMapping("/testDb")
    public List<TestEntity> TestDb() {
        List<TestEntity> testEntityList = testMapper.selectList(null);
        //IPage<TestEntity> page= testMapper.selectPage(new Page<>(1,10),null);

        return testEntityList;
        //return "consumer testGateWay, i'm provider ,my port:" + port;
    }
}
