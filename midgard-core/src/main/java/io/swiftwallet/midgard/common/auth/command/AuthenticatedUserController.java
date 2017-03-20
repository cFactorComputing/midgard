package io.swiftwallet.midgard.common.auth.command;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class AuthenticatedUserController {

    @Inject
    private AuthenticatedUserFacade authenticatedUserFacade;

    @RequestMapping(value = "/command/logout", method = RequestMethod.POST)
    private void logout(@RequestParam("access_token") final String accessToken){
        authenticatedUserFacade.logout(accessToken);
    }

}
