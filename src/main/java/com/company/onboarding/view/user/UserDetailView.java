package com.company.onboarding.view.user;

import com.company.onboarding.entity.OnboardingStatus;
import com.company.onboarding.entity.Step;
import com.company.onboarding.entity.User;
import com.company.onboarding.entity.UserStep;
import com.company.onboarding.view.DataGridHelper;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.core.DataManager;
import io.jmix.core.EntityStates;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionPropertyContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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

    @Autowired
    private DataManager dataManager;
    @ViewComponent
    private CollectionPropertyContainer<UserStep> stepsDc;
    @ViewComponent
    private DataContext dataContext;
    @ViewComponent
    private DataGrid<UserStep> stepsDataGrid;
    @Autowired
    private UiComponents uiComponents;

    @ViewComponent
    private FormLayout form;

    private Image image;

    @Autowired
    private FileStorage fileStorage;

    @Subscribe
    public void onInit(InitEvent event) {
        timeZoneField.setItems(List.of(TimeZone.getAvailableIDs()));

        Grid.Column<UserStep> checkboxColumn = stepsDataGrid.addColumn(new ComponentRenderer<>(userStep -> {
                    Checkbox checkbox = uiComponents.create(Checkbox.class);
                    checkbox.setValue(userStep.getCompletedDate() != null);
                    checkbox.addValueChangeListener(e -> {
                        if (userStep.getCompletedDate() == null) {
                            userStep.setCompletedDate(LocalDate.now());
                        } else {
                            userStep.setCompletedDate(null);
                        }
                    });
                    return checkbox;
                }))
                .setWidth("50px"); // width doesn't work

        DataGridHelper.setDataGridColumnPosition(stepsDataGrid, checkboxColumn, 0);

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setMaxFiles(1);
        upload.addFinishedListener(event1 -> {
            FileRef fileRef = fileStorage.saveStream(memoryBuffer.getFileName(), memoryBuffer.getInputStream());
            getEditedEntity().setPicture(fileRef);
            updateImage();
        });
        form.add(upload);

        image = new Image();
        image.setHeight("200px");
        image.setWidth("200px");
        image.addClassName("user-picture");
        form.add(image);
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<User> event) {
        usernameField.setReadOnly(false);
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);

        event.getEntity().setOnboardingStatus(OnboardingStatus.NOT_STARTED);
    }

    @Subscribe
    public void onReady(ReadyEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            usernameField.focus();
        }

        updateImage();
    }

    private void updateImage() {
        FileRef fileRef = getEditedEntity().getPicture();
        if (fileRef != null) {
            StreamResource streamResource = new StreamResource(
                    fileRef.getFileName(),
                    () -> fileStorage.openStream(fileRef));
            image.setSrc(streamResource);
        }
    }

    @Subscribe
    protected void onBeforeCommit(BeforeSaveEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            if (!Objects.equals(passwordField.getValue(), confirmPasswordField.getValue())) {
                notifications.create(messageBundle.getMessage("passwordsDoNotMatch"))
                        .withType(Notifications.Type.WARNING)
                        .show();
                event.preventSave();
            }
            getEditedEntity().setPassword(passwordEncoder.encode(passwordField.getValue()));
        }
    }

    @Subscribe("generateStepsButton")
    public void onGenerateStepsButtonClick(ClickEvent<Button> event) {
        User user = getEditedEntity();

        if (user.getJoiningDate() == null) {
            notifications.show("Cannot generate steps for user without 'Joining date'");
            return;
        }

        List<Step> steps = dataManager.load(Step.class)
                .query("select s from Step s order by s.sortValue asc")
                .list();

        for (Step step : steps) {
            if (stepsDc.getItems().stream().noneMatch(userStep ->
                    userStep.getStep().equals(step))) {
                UserStep userStep = dataContext.create(UserStep.class);
                userStep.setUser(user);
                userStep.setStep(step);
                userStep.setDueDate(user.getJoiningDate().plusDays(step.getDuration()));
                userStep.setSortValue(step.getSortValue());
                stepsDc.getMutableItems().add(userStep);
            }
        }
    }

    @Subscribe(id = "stepsDc", target = Target.DATA_CONTAINER)
    public void onStepsDcCollectionChange(CollectionContainer.CollectionChangeEvent<UserStep> event) {
        updateOnboardingStatus();
    }

    @Subscribe(id = "stepsDc", target = Target.DATA_CONTAINER)
    public void onStepsDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<UserStep> event) {
        updateOnboardingStatus();
    }

    private void updateOnboardingStatus() {
        User user = getEditedEntity();

        long completedCount = user.getSteps() == null ? 0 :
                user.getSteps().stream()
                        .filter(us -> us.getCompletedDate() != null)
                        .count();
        if (completedCount == 0) {
            user.setOnboardingStatus(OnboardingStatus.NOT_STARTED);
        } else if (completedCount == user.getSteps().size()) {
            user.setOnboardingStatus(OnboardingStatus.COMPLETED);
        } else {
            user.setOnboardingStatus(OnboardingStatus.IN_PROGRESS);
        }
    }
}