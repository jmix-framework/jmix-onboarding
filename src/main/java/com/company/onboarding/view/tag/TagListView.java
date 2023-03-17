package com.company.onboarding.view.tag;

import com.company.onboarding.entity.Tag;

import com.company.onboarding.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "tags", layout = MainView.class)
@ViewController("Tag.list")
@ViewDescriptor("tag-list-view.xml")
@LookupComponent("tagsTable")
@DialogMode(width = "50em", height = "37.5em")
public class TagListView extends StandardListView<Tag> {
}