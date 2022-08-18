package com.company.onboarding.screen.department;

import io.jmix.ui.screen.*;
import com.company.onboarding.entity.Department;

@UiController("Department.browse")
@UiDescriptor("department-browse.xml")
@LookupComponent("departmentsTable")
public class DepartmentBrowse extends StandardLookup<Department> {
}