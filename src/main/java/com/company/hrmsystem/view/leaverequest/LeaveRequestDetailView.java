package com.company.hrmsystem.view.leaverequest;

import com.company.hrmsystem.entity.LeaveRequest;
import com.company.hrmsystem.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "leave-requests/:id", layout = MainView.class)
@ViewController(id = "LeaveRequest.detail")
@ViewDescriptor(path = "leave-request-detail-view.xml")
@EditedEntityContainer("leaveRequestDc")
@DialogMode(width = "64em")
public class LeaveRequestDetailView extends StandardDetailView<LeaveRequest> {

}