package com.company.hrmsystem.view.leaverequest;

import com.company.hrmsystem.entity.LeaveRequest;
import com.company.hrmsystem.view.main.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "leave-requests", layout = MainView.class)
@ViewController(id = "LeaveRequest.list")
@ViewDescriptor(path = "leave-request-list-view.xml")
@LookupComponent("leaveRequestsDataGrid")
@DialogMode(width = "64em")
public class LeaveRequestListView extends StandardListView<LeaveRequest> {

    @Subscribe("leaveRequestsDataGrid")
    public void onLeaveRequestsDataGridItemDoubleClick(final ItemDoubleClickEvent<LeaveRequest> event) {
        LeaveRequest leaveRequest = event.getItem();
        if (leaveRequest != null) {
            // Navigate to the detail view with the selected item
            UI.getCurrent().navigate(
                    LeaveRequestDetailView.class,
                    new RouteParameters("id", leaveRequest.getId().toString())
            );
        }
    }
}