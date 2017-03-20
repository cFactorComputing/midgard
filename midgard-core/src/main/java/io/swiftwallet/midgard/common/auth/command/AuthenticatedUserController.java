package io.swiftwallet.midgard.common.auth.command;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class AuthenticatedUserController {
    private AuthenticatedUserFacade authenticatedUserFacade;

    @Inject
    public AuthenticatedUserController(final AuthenticatedUserFacade authenticatedUserFacade) {
        this.authenticatedUserFacade = authenticatedUserFacade;
    }

    @PreAuthorize("#oauth2.hasScope('write') and hasRole('ROLE_USER')")
    @RequestMapping(value = "/command/logout", method = RequestMethod.POST)
    private void logout(@RequestParam("access_token") final String accessToken) {
        authenticatedUserFacade.logout(accessToken);
    }

}