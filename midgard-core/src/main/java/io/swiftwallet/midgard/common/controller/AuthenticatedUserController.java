package io.swiftwallet.midgard.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class AuthenticatedUserController {

    @RequestMapping(value = "/command/logout", method = RequestMethod.POST)
    private void logout(){
        //TODO
    }

}
