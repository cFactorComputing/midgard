package io.swiftwallet.midgard.common.user;

import io.swiftwallet.common.domain.security.WalletUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class CurrentUserController {

    @Inject
    private CurrentUserFacade currentUserFacade;

    @RequestMapping(value = "/query/currentuser", method = RequestMethod.GET)
    private WalletUser retrieveCurrentUser() {
        return currentUserFacade.retrieveCurrentUser();
    }
}
