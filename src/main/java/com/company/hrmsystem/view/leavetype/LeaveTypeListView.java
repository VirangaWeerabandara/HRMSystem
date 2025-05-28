package com.company.hrmsystem.view.leavetype;

import com.company.hrmsystem.entity.LeaveType;
import com.company.hrmsystem.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "leave-types", layout = MainView.class)
@ViewController(id = "LeaveType.list")
@ViewDescriptor(path = "leave-type-list-view.xml")
@LookupComponent("leaveTypesDataGrid")
@DialogMode(width = "64em")
public class LeaveTypeListView extends StandardListView<LeaveType> {
}