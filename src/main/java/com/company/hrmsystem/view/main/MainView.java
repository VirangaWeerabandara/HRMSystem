package com.company.hrmsystem.view.main;

import com.company.hrmsystem.entity.User;
import com.company.hrmsystem.service.UserLeaveSummaryService;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Route("")
@ViewController(id = "MainView")
@ViewDescriptor(path = "main-view.xml")
public class MainView extends StandardMainView {
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private UserLeaveSummaryService userLeaveSummaryService;

    @ViewComponent
    private Label sickLeaveLabel;

    @ViewComponent
    private Label casualLeaveLabel;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        User user = (User) currentAuthentication.getUser();
        Map<String, Integer> leaveTypeCounts = userLeaveSummaryService.getLeaveTypeCountsForUser(user);

        sickLeaveLabel.setText("Sick Leave: " + leaveTypeCounts.getOrDefault("Sick Leave", 0));
        casualLeaveLabel.setText("Casual Leave: " + leaveTypeCounts.getOrDefault("Casual Leave", 0));
    }
}