package com.company.onboarding.view.user;

import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "users", layout = MainView.class)
@ViewController("User.list")
@ViewDescriptor("user-list-view.xml")
@LookupComponent("usersTable")
@DialogMode(width = "50em", height = "37.5em")
public class UserListView extends StandardListView<User> {
}