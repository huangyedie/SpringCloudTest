package consumer.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "provider")
public interface ProviderClient {
    @GetMapping("/providerTest/hi")
    String hi(@RequestParam(value = "name", defaultValue = "forezp", required = false) String name);
}
