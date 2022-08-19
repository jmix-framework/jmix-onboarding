package com.company.onboarding.screen.user;

import com.company.onboarding.entity.User;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.FileStorageResource;
import io.jmix.ui.component.Image;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {

    @Autowired
    private UiComponents uiComponents;

    @Install(to = "usersTable.picture", subject = "columnGenerator")
    private Component usersTablePictureColumnGenerator(User user) {
        if (user.getPicture() != null) {
            Image image = uiComponents.create(Image.class);
            image.setScaleMode(Image.ScaleMode.CONTAIN);
            image.setSource(FileStorageResource.class).setFileReference(user.getPicture());
            image.setWidth("30px");
            image.setHeight("30px");
            return image;
        } else {
            return null;
        }
    }
}