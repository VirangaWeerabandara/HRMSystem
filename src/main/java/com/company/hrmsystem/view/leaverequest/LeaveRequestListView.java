package com.company.hrmsystem.view.leaverequest;

import com.company.hrmsystem.entity.LeaveRequest;
import com.company.hrmsystem.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "leave-requests", layout = MainView.class)
@ViewController(id = "LeaveRequest.list")
@ViewDescriptor(path = "leave-request-list-view.xml")
@LookupComponent("leaveRequestsDataGrid")
@DialogMode(width = "64em")
public class LeaveRequestListView extends StandardListView<LeaveRequest> {
}