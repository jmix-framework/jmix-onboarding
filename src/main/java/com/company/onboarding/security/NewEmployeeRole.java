package com.company.onboarding.security;

import com.company.onboarding.entity.Step;
import com.company.onboarding.entity.User;
import com.company.onboarding.entity.UserStep;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;

@ResourceRole(name = "New Employee", code = "new-employee", scope = "UI")
public interface NewEmployeeRole {

//    @MenuPolicy(menuIds = "MyOnboardingScreen")
//    @ViewPolicy(viewIds = "MyOnboardingScreen")
    void screens();

    @EntityAttributePolicy(entityClass = Step.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Step.class, actions = EntityPolicyAction.READ)
    void step();

    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void user();

    @EntityAttributePolicy(entityClass = UserStep.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = UserStep.class, actions = {EntityPolicyAction.UPDATE, EntityPolicyAction.READ})
    void userStep();
}