package com.company.hrmsystem.view.main;

import com.company.hrmsystem.entity.User;
import com.company.hrmsystem.entity.UserLeaveStats;
import com.company.hrmsystem.service.UserLeaveStatsService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.company.hrmsystem.entity.LeaveRequest;
import com.company.hrmsystem.entity.LeaveStatus;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.Map;

@Route("")
@ViewController(id = "MainView")
@ViewDescriptor(path = "main-view.xml")
public class MainView extends StandardMainView {
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private UserLeaveStatsService userLeaveStatsService;

    @ViewComponent
    private Span totalLeavesLabel;

    @ViewComponent
    private Div availableLeavesContainer;

    @ViewComponent
    private Div consumedLeavesContainer;

    @ViewComponent
    private Div pendingLeavesContainer;

    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        User user = (User) currentAuthentication.getUser();

        // Get comprehensive leave stats
        UserLeaveStats leaveStats = userLeaveStatsService.getUserLeaveStats(user.getId());

        // Set total leaves info
        int totalLeaves = leaveStats.getTotalLeaves() != null ? leaveStats.getTotalLeaves() : 0;
        totalLeavesLabel.setText(totalLeaves + " days");

        // Display available leaves
        populateLeaveContainer(availableLeavesContainer, leaveStats.getAvailableLeaves(), "green");

        // Display consumed leaves
        populateLeaveContainer(consumedLeavesContainer, leaveStats.getConsumedLeaves(), "blue");

        // Display pending leaves
        populateLeaveContainer(pendingLeavesContainer, leaveStats.getPendingLeaves(), "orange");
    }

    private void populateLeaveContainer(Div container, Map<String, Integer> leaveMap, String color) {
        container.removeAll();

        if (leaveMap == null || leaveMap.isEmpty()) {
            Span noDataLabel = new Span("No data available");
            noDataLabel.getStyle().set("font-style", "italic");
            noDataLabel.getStyle().set("text-align", "center");
            container.add(noDataLabel);
            return;
        }

        leaveMap.forEach((type, days) -> {
            HorizontalLayout row = new HorizontalLayout();
            row.addClassName("leave-stat-row");
            row.setWidthFull();

            Span typeLabel = new Span(type);
            typeLabel.addClassName("leave-type-label");

            Span daysLabel = new Span(days + " days");
            daysLabel.addClassName("leave-days-count");

            // Add specific class based on container type
            if (container == availableLeavesContainer) {
                daysLabel.addClassName("available");
                daysLabel.getStyle().set("color", "#2e7d32"); // Dark green
            } else if (container == consumedLeavesContainer) {
                daysLabel.addClassName("consumed");
                daysLabel.getStyle().set("color", "#1565c0"); // Dark blue
            } else if (container == pendingLeavesContainer) {
                daysLabel.addClassName("pending");
                daysLabel.getStyle().set("color", "#e65100"); // Dark orange
            } else {
                daysLabel.getStyle().set("color", color);
            }

            row.add(typeLabel, daysLabel);

            // Add double-click functionality only to consumed leaves
            if (container == consumedLeavesContainer) {
                row.getStyle().set("cursor", "pointer");
                row.addClassName("clickable-row");
                row.addDoubleClickListener(event -> showLeaveDetails(type));
            }

            container.add(row);
        });
    }

    private void showLeaveDetails(String leaveType) {
        User currentUser = (User) currentAuthentication.getUser();

        // Fetch leave details for this type
        List<LeaveRequest> leaveRequests = dataManager.load(LeaveRequest.class)
                .query("select e from LeaveRequest e where " +
                        "e.user = :user and " +
                        "e.leaveType.name = :leaveType and " +
                        "e.leaveStatus = :status " +
                        "order by e.startDate desc")
                .parameter("user", currentUser)
                .parameter("leaveType", leaveType)
                .parameter("status", LeaveStatus.APPROVED.getId())
                .list();

        // Create dialog to show details
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Details for " + leaveType);
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);

        Grid<LeaveRequest> grid = new Grid<>();
        grid.setItems(leaveRequests);
        grid.addColumn(request -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            return request.getStartDate() != null ? request.getStartDate().format(formatter) : "";
        }).setHeader("Start Date").setSortable(true);

        grid.addColumn(request -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            return request.getEndDate() != null ? request.getEndDate().format(formatter) : "";
        }).setHeader("End Date").setSortable(true);

        grid.addColumn(LeaveRequest::getWorkingDays).setHeader("Days").setSortable(true);
        grid.addColumn(LeaveRequest::getReason).setHeader("Reason").setFlexGrow(1);

        grid.setHeight("300px");
        content.add(grid);

        Button closeButton = new Button("Close", e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout actions = new HorizontalLayout(closeButton);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.setWidth("100%");

        content.add(actions);
        dialog.add(content);

        dialog.open();
    }
}