package com.company.hrmsystem.view.leaverequest;

import com.company.hrmsystem.entity.LeaveRequest;
import com.company.hrmsystem.entity.LeaveStatus;
import com.company.hrmsystem.entity.User;
import com.company.hrmsystem.service.LeaveRequestService;
import com.company.hrmsystem.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.component.validation.ValidationErrors;
import io.jmix.core.validation.group.UiCrossFieldChecks;
import io.jmix.flowui.action.SecuredBaseAction;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.*;
import io.jmix.flowui.view.*;
import io.jmix.flowui.Dialogs;
import io.jmix.core.security.CurrentAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import com.company.hrmsystem.entity.LeaveType;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Map;

@Route(value = "create-leave-requests", layout = MainView.class)
@ViewController(id = "CreateLeaveRequest.list")
@ViewDescriptor(path = "create-leave-request-list-view.xml")
@LookupComponent("leaveRequestsDataGrid")
@DialogMode(width = "64em")
public class CreateLeaveRequestListView extends StandardListView<LeaveRequest> {

    @ViewComponent
    private DataContext dataContext;

    @ViewComponent
    private CollectionContainer<LeaveRequest> leaveRequestsDc;

    @ViewComponent
    private InstanceContainer<LeaveRequest> leaveRequestDc;

    @ViewComponent
    private InstanceLoader<LeaveRequest> leaveRequestDl;

    @ViewComponent
    private CollectionLoader<LeaveRequest> leaveRequestsDl;

    @ViewComponent
    private VerticalLayout listLayout;

    @ViewComponent
    private DataGrid<LeaveRequest> leaveRequestsDataGrid;

    @ViewComponent
    private FormLayout form;

    @ViewComponent
    private HorizontalLayout detailActions;

    @ViewComponent
    private CollectionLoader<LeaveType> leaveTypesDl;

    @ViewComponent
    private CollectionContainer<LeaveType> leaveTypesDc;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Subscribe
    public void onInit(final InitEvent event) {
        leaveRequestsDataGrid.getActions().forEach(action -> {
            if (action instanceof SecuredBaseAction secured) {
                secured.addEnabledRule(() -> listLayout.isEnabled());
            }
        });
    }

    @ViewComponent
    private ComboBox<LeaveType> leaveTypeField;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        User currentUser = getCurrentUser();
        leaveRequestsDl.setParameter("currentUser", currentUser);
        leaveRequestsDl.load();

        // Load leave types
        leaveTypesDl.load();

        // Debug: Print loaded types
        System.out.println("Loaded leave types: " + leaveTypesDc.getItems().size());

        // Set items directly on the comboBox
        leaveTypeField.setItems(leaveTypesDc.getItems());

        // Configure custom renderer for leaveStatus column
        leaveRequestsDataGrid.getColumnByKey("leaveStatus").setRenderer(
                new ComponentRenderer<>(leaveRequest -> {
                    LeaveStatus status = leaveRequest.getLeaveStatus();
                    Span statusSpan = new Span(status != null ? status.name() : "");

                    if (status != null) {
                        switch (status) {
                            case APPROVED:
                                statusSpan.getElement().getStyle().set("color", "green");
                                statusSpan.getElement().getStyle().set("font-weight", "bold");
                                break;
                            case REJECTED:
                                statusSpan.getElement().getStyle().set("color", "red");
                                statusSpan.getElement().getStyle().set("font-weight", "bold");
                                break;
                            case PENDING:
                                statusSpan.getElement().getStyle().set("color", "#FFA500"); // Orange/Yellow
                                statusSpan.getElement().getStyle().set("font-weight", "bold");
                                break;
                        }
                    }

                    return statusSpan;
                })
        );

        updateControls(false);
    }

    @Subscribe("leaveRequestsDataGrid.createAction")
    public void onLeaveRequestsDataGridCreateAction(final ActionPerformedEvent event) {
        dataContext.clear();
        LeaveRequest entity = dataContext.create(LeaveRequest.class);

        // Get the current logged in user
        User currentUser = getCurrentUser();

        // Set the user as the current logged in user
        entity.setUser(currentUser);

        // Set the approver as the manager of the current user
        entity.setApprover(currentUser.getManager());

        // Set the default status to PENDING
        entity.setLeaveStatus(LeaveStatus.PENDING);

        leaveRequestDc.setItem(entity);
        updateControls(true);
    }

    private User getCurrentUser() {
        return (User) currentAuthentication.getUser();
    }

    @Subscribe("leaveRequestsDataGrid.editAction")
    public void onLeaveRequestsDataGridEditAction(final ActionPerformedEvent event) {
        updateControls(true);
    }

    @Autowired
    private com.company.hrmsystem.service.UserLeaveSummaryService userLeaveSummaryService;

    @Autowired
    private Dialogs dialogs;

    @Autowired
    private DataManager dataManager;

    @Subscribe("saveButton")
    public void onSaveButtonClick(final ClickEvent<JmixButton> event) {
        leaveRequestsDl.load();
        LeaveRequest item = leaveRequestDc.getItem();
        ValidationErrors validationErrors = validateView(item);
        if (!validationErrors.isEmpty()) {
            ViewValidation viewValidation = getViewValidation();
            viewValidation.showValidationErrors(validationErrors);
            viewValidation.focusProblemComponent(validationErrors);
            return;
        }
        calculateWorkingDays(item);

        User user = (User) currentAuthentication.getUser();
        // Reload user from DB to get the latest totalLeaves
        user = dataManager.load(User.class).id(user.getId()).one();
        Integer requestedDays = item.getWorkingDays() != null ? item.getWorkingDays() : 0;

        String leaveTypeName = item.getLeaveType() != null ? item.getLeaveType().getName() : null;
        Map<String, Integer> leaveTypeCounts = userLeaveSummaryService.getLeaveTypeCountsForUser(user);
        Integer availableTypeDays = leaveTypeName != null ? leaveTypeCounts.getOrDefault(leaveTypeName, 0) : 0;

        if (requestedDays > availableTypeDays) {
            dialogs.createMessageDialog()
                    .withHeader("Insufficient " + leaveTypeName + " Balance")
                    .withText("You have only " + availableTypeDays + " " + leaveTypeName + " days left, but you requested " + requestedDays + " days.")
                    .open();
            return;
        }

        dataContext.save();
        leaveRequestsDc.replaceItem(item);
        updateControls(false);
    }

    @Subscribe("cancelButton")
    public void onCancelButtonClick(final ClickEvent<JmixButton> event) {
        dataContext.clear();
        leaveRequestDc.setItem(null);
        leaveRequestDl.load();
        updateControls(false);
    }

    @Subscribe(id = "leaveRequestsDc", target = Target.DATA_CONTAINER)
    public void onLeaveRequestsDcItemChange(final InstanceContainer.ItemChangeEvent<LeaveRequest> event) {
        LeaveRequest entity = event.getItem();
        dataContext.clear();
        if (entity != null) {
            leaveRequestDl.setEntityId(entity.getId());
            leaveRequestDl.load();
        } else {
            leaveRequestDl.setEntityId(null);
            leaveRequestDc.setItem(null);
        }
        updateControls(false);
    }

    protected ValidationErrors validateView(LeaveRequest entity) {
        ViewValidation viewValidation = getViewValidation();
        ValidationErrors validationErrors = viewValidation.validateUiComponents(form);
        if (!validationErrors.isEmpty()) {
            return validationErrors;
        }
        validationErrors.addAll(viewValidation.validateBeanGroup(UiCrossFieldChecks.class, entity));
        return validationErrors;
    }

    private void updateControls(boolean editing) {
        UiComponentUtils.getComponents(form).forEach(component -> {
            if (component instanceof HasValueAndElement<?, ?> field) {
                field.setReadOnly(!editing);
            }
        });

        // Make sure the ComboBox is explicitly enabled when in edit mode
        if (leaveTypeField != null) {
            leaveTypeField.setReadOnly(!editing);
        }

        detailActions.setVisible(editing);
        listLayout.setEnabled(!editing);
        leaveRequestsDataGrid.getActions().forEach(Action::refreshState);
    }

    private ViewValidation getViewValidation() {
        return getApplicationContext().getBean(ViewValidation.class);
    }


    @Subscribe("startDateField")
    public void onStartDateFieldValueChange(final HasValue.ValueChangeEvent<?> event) {
        updateWorkingDays();
    }

    @Subscribe("endDateField")
    public void onEndDateFieldValueChange(final HasValue.ValueChangeEvent<?> event) {
        updateWorkingDays();
    }

    private void updateWorkingDays() {
        LeaveRequest item = leaveRequestDc.getItem();
        if (item != null && item.getStartDate() != null && item.getEndDate() != null) {
            calculateWorkingDays(item);
        }
    }

    private void calculateWorkingDays(LeaveRequest leaveRequest) {
        // Get the LeaveRequestService from the application context
        LeaveRequestService leaveRequestService = getApplicationContext().getBean(LeaveRequestService.class);

        // Calculate working days
        Integer workingDays = leaveRequestService.calculateWorkingDays(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate());

        // Set the calculated working days
        leaveRequest.setWorkingDays(workingDays);
    }
}
