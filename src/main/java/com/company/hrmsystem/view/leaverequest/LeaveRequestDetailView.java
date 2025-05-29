package com.company.hrmsystem.view.leaverequest;

import com.company.hrmsystem.entity.LeaveRequest;
import com.company.hrmsystem.service.LeaveRequestService;
import com.company.hrmsystem.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.datepicker.TypedDatePicker;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "leave-requests/:id", layout = MainView.class)
@ViewController(id = "LeaveRequest.detail")
@ViewDescriptor(path = "leave-request-detail-view.xml")
@EditedEntityContainer("leaveRequestDc")
@DialogMode(width = "64em")
public class LeaveRequestDetailView extends StandardDetailView<LeaveRequest> {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Subscribe(id = "saveAndCloseButton", subject = "clickListener")
    public void onSaveAndCloseButtonClick(final ClickEvent<JmixButton> event) {
        UI.getCurrent().navigate(LeaveRequestListView.class);
    }

}