package com.company.onboarding.listener;

import com.company.onboarding.entity.OnboardingStatus;
import com.company.onboarding.entity.User;
import com.company.onboarding.entity.UserStep;
import io.jmix.core.DataManager;
import io.jmix.core.Id;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserStepEventListener {

    @Autowired
    private DataManager dataManager;

    @EventListener
    public void onUserStepChangedBeforeCommit(EntityChangedEvent<UserStep> event) {
        if (event.getType() == EntityChangedEvent.Type.CREATED || event.getType() == EntityChangedEvent.Type.UPDATED) {
            Id<UserStep> userStepId = event.getEntityId();

            UserStep userStep = dataManager.load(userStepId).one();
            User user = userStep.getUser();
            List<UserStep> allUserSteps = user.getSteps();

            long completedCount = allUserSteps.stream().filter(us -> us.getCompletedDate() != null).count();
            if (completedCount == 0) {
                user.setOnboardingStatus(OnboardingStatus.NOT_STARTED);
            } else if (completedCount == allUserSteps.size()) {
                user.setOnboardingStatus(OnboardingStatus.COMPLETED);
            } else {
                user.setOnboardingStatus(OnboardingStatus.IN_PROGRESS);
            }

            dataManager.save(user);
        }
    }
}