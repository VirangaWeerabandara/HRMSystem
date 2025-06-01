package com.company.hrmsystem.view.main;

import com.company.hrmsystem.entity.User;
import com.company.hrmsystem.service.UserLeaveSummaryService;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.html.Span;

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
    private Span sickLeaveLabel;

    @ViewComponent
    private Span casualLeaveLabel;

    @ViewComponent
    private Span totalLeavesLabel;

    @ViewComponent
    private Span proratedSickLeaveLabel;

    @ViewComponent
    private Span proratedCasualLeaveLabel;

    @ViewComponent
    private  Span test;

    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        User user = (User) currentAuthentication.getUser();
        user = dataManager.load(User.class).id(user.getId()).one();

        Map<String, Integer> availableLeaves = userLeaveSummaryService.getAvailableLeaveForUser(user);
        Map<String, Integer> proratedLeaves = userLeaveSummaryService.getProratedAvailableLeavesForCurrentUser(user);

        totalLeavesLabel.setText("Total Leaves: " + (user.getTotalLeaves() != null ? user.getTotalLeaves() : 0));
        sickLeaveLabel.setText("Sick Leave Available: " + availableLeaves.getOrDefault("Sick", 0));
        casualLeaveLabel.setText("Casual Leave Available: " + availableLeaves.getOrDefault("Casual", 0));

        proratedSickLeaveLabel.setText("Prorated Sick Leave: " + proratedLeaves.getOrDefault("Sick", 0));
        proratedCasualLeaveLabel.setText("Prorated Casual Leave: " + proratedLeaves.getOrDefault("Casual", 0));
    }
}