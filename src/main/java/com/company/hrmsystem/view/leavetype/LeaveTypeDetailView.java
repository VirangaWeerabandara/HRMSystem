package com.company.hrmsystem.view.leavetype;

import com.company.hrmsystem.entity.LeaveType;
import com.company.hrmsystem.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "leave-types/:id", layout = MainView.class)
@ViewController(id = "LeaveType.detail")
@ViewDescriptor(path = "leave-type-detail-view.xml")
@EditedEntityContainer("leaveTypeDc")
public class LeaveTypeDetailView extends StandardDetailView<LeaveType> {
}