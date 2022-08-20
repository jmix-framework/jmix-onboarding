package com.company.onboarding.screen.myonboarding;

import com.company.onboarding.entity.UserStep;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.CheckBox;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.Label;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.DataContext;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@UiController("MyOnboardingScreen")
@UiDescriptor("my-onboarding-screen.xml")
public class MyOnboardingScreen extends Screen {

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private CollectionLoader<UserStep> userStepsDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DataContext dataContext;
    @Autowired
    private CollectionContainer<UserStep> userStepsDc;
    @Autowired
    private Label totalStepsLabel;
    @Autowired
    private Label completedStepsLabel;
    @Autowired
    private Label overdueStepsLabel;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        userStepsDl.setParameter("user", currentAuthentication.getUser());
        userStepsDl.load();

        updateLabels();
    }

    @Install(to = "userStepsTable.completed", subject = "columnGenerator")
    private Component userStepsTableCompletedColumnGenerator(UserStep userStep) {
        CheckBox checkBox = uiComponents.create(CheckBox.class);
        checkBox.setValue(userStep.getCompletedDate() != null);
        checkBox.addValueChangeListener(e -> {
            if (userStep.getCompletedDate() == null) {
                userStep.setCompletedDate(LocalDate.now());
            } else {
                userStep.setCompletedDate(null);
            }
        });
        return checkBox;
    }

    @Subscribe("saveButton")
    public void onSaveButtonClick(Button.ClickEvent event) {
        dataContext.commit();
        close(StandardOutcome.COMMIT);
    }

    @Subscribe("discardButton")
    public void onDiscardButtonClick(Button.ClickEvent event) {
        close(StandardOutcome.DISCARD);
    }

    @Subscribe(id = "userStepsDc", target = Target.DATA_CONTAINER)
    public void onUserStepsDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<UserStep> event) {
        updateLabels();
    }

    private void updateLabels() {
        totalStepsLabel.setValue("Total steps: " + userStepsDc.getItems().size());

        long completedCount = userStepsDc.getItems().stream().filter(us -> us.getCompletedDate() != null).count();
        completedStepsLabel.setValue("Completed steps: " + completedCount);

        long overdueCount = userStepsDc.getItems().stream()
                .filter(us -> us.getCompletedDate() == null
                        && us.getDueDate() != null
                        && us.getDueDate().isBefore(LocalDate.now()))
                .count();
        overdueStepsLabel.setValue("Overdue steps: " + overdueCount);
    }
}