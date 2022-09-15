package com.company.onboarding.view.user;

import com.company.onboarding.entity.OnboardingStatus;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import io.jmix.core.EntityStates;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Route(value = "users/:id", layout = MainView.class)
@ViewController("User.detail")
@ViewDescriptor("user-detail-view.xml")
@EditedEntityContainer("userDc")
public class UserDetailView extends StandardDetailView<User> {

    @ViewComponent
    private TypedTextField<String> usernameField;
    @ViewComponent
    private PasswordField passwordField;
    @ViewComponent
    private PasswordField confirmPasswordField;
    @ViewComponent
    private ComboBox<String> timeZoneField;

    @Autowired
    private EntityStates entityStates;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private Notifications notifications;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Subscribe
    public void onInit(InitEvent event) {
        timeZoneField.setItems(List.of(TimeZone.getAvailableIDs()));
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<User> event) {
        usernameField.setReadOnly(false);
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);

        event.getEntity().setOnboardingStatus(OnboardingStatus.NOT_STARTED);
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            usernameField.focus();
        }
    }

    @Subscribe
    protected void onBeforeCommit(BeforeCommitChangesEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            if (!Objects.equals(passwordField.getValue(), confirmPasswordField.getValue())) {
                notifications.create(messageBundle.getMessage("passwordsDoNotMatch"))
                        .withType(Notifications.Type.WARNING)
                        .show();
                event.preventCommit();
            }
            getEditedEntity().setPassword(passwordEncoder.encode(passwordField.getValue()));
        }
    }
}