package com.company.onboarding.view.tag;

import com.company.onboarding.entity.Tag;

import com.company.onboarding.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "tags/:id", layout = MainView.class)
@ViewController("Tag.detail")
@ViewDescriptor("tag-detail-view.xml")
@EditedEntityContainer("tagDc")
public class TagDetailView extends StandardDetailView<Tag> {
}