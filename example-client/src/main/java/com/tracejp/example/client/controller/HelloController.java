package com.tracejp.example.client.controller;

import com.tracejp.exmaple.service.api.HelloService;
import com.tracejp.exmaple.service.api.domain.Hello;
import com.tracejp.yozu.rpc.core.annotation.YozuRpcAutowired;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> client 入口 <p/>
 *
 * @author traceJP
 * @since 2023/10/30 16:21
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class HelloController {

    @YozuRpcAutowired
    private HelloService helloService;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        try {
            String echo = helloService.echo(hello);
            log.info("echo: {}", echo);
            return echo;
        } catch (Exception e) {
            return "ERROR: " + e;
        }
    }

}
