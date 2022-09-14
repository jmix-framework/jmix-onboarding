package com.company.onboarding.view.login;

import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.AccessDeniedException;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import io.jmix.securityflowui.authentication.AuthDetails;
import io.jmix.securityflowui.authentication.LoginViewSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

@Route(value = "login")
@ViewController("LoginView")
@ViewDescriptor("login-view.xml")
public class LoginView extends StandardView {

    private final Logger log = LoggerFactory.getLogger(LoginView.class);

    @Autowired
    private LoginViewSupport loginViewSupport;

    @Subscribe("login")
    public void onLogin(LoginEvent event) {
        try {
            loginViewSupport.authenticate(
                    AuthDetails.of(event.getUsername(), event.getPassword())
            );
        } catch (BadCredentialsException | DisabledException | LockedException | AccessDeniedException e) {
            log.info("Login failed", e);
            event.getSource().setError(true);
        }
    }
}
