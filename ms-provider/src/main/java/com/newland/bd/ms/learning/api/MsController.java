package com.newland.bd.ms.learning.api;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Created by hxw on 2017/12/27.
 */
@RestController
@RequestMapping("test")
public class MsController {
    private static final Logger logger = LoggerFactory.getLogger(MsController.class);

    @GetMapping("/hello")
    @HystrixCommand(fallbackMethod = "helloFallback")
    public String hello() {
        return "provide hello world";
    }

    @GetMapping("/hellosleep")
    /*@HystrixCommand(fallbackMethod = "helloexFallback",threadPoolKey="haha",commandProperties={@HystrixProperty(name="metrics.healthSnapshot.intervalInMilliseconds",value="9999")
            ,@HystrixProperty(name="hystrix.command.default.metrics.healthSnapshot.intervalInMilliseconds",value="8888")
            ,@HystrixProperty(name="hystrix.command.HystrixCommandKey.metrics.healthSnapshot.intervalInMilliseconds",value="7777")

    })*/
    @HystrixCommand(fallbackMethod = "helloexFallback",threadPoolKey="haha",commandProperties={@HystrixProperty(name="metrics.healthSnapshot.intervalInMilliseconds",value="3000")

    })
    public String hellosleep() throws InterruptedException {
        int i = 2000;
        TimeUnit.MILLISECONDS.sleep(i);
        return "provide hellosleep world!";
    }

    public String helloexFallback() {
        return "fallback helloex.";
    }

    @GetMapping("/hellopex")
    @HystrixCommand(fallbackMethod = "helloFallbackMethod")
    public String helloex(@RequestParam String str) {
        logger.info("☆☆☆ helloex before!");
        int i = 20;
        logger.info("☆☆☆ i = {}", i);
        try {
            TimeUnit.MILLISECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("☆☆☆ helloex after!");
        return "provide helloex world!";
    }

    public String helloexFallbackWithParameter(String str) {
        return "fallback 参数值为:" + str;
    }

    @GetMapping("/hellotex")
    @HystrixCommand(fallbackMethod = "hellotexFallback")
    public String hellotex() {
        logger.info("☆☆☆ helloex before!");
        throw new RuntimeException("aaaa");
    }

    public String hellotexFallback() {
        return "抛异常的fallback";
    }
}
