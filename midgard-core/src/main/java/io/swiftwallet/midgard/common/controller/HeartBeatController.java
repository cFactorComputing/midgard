package io.swiftwallet.midgard.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class HeartBeatController {

    @RequestMapping(value = "/query/heartbeat", method = RequestMethod.GET)
    private Date heartBeat(){
        return new Date();
    }
}
