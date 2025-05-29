package com.company.hrmsystem.listener;

import com.company.hrmsystem.entity.LeaveType;
import com.company.hrmsystem.service.LeaveCalculationService;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class LeaveTypeChangedListener {

    @Autowired
    private LeaveCalculationService leaveCalculationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLeaveTypeChangedAfterCommit(EntityChangedEvent<LeaveType> event) {
        // Only trigger recalculation for specific change types
        EntityChangedEvent.Type changeType = event.getType();

        if (changeType == EntityChangedEvent.Type.CREATED ||
                changeType == EntityChangedEvent.Type.UPDATED) {

            System.out.println("Significant LeaveType change detected: " + changeType);

            // For updates, check if relevant fields changed
            if (changeType == EntityChangedEvent.Type.UPDATED) {
                boolean relevantFieldChanged = event.getChanges().isChanged("active") ||
                        event.getChanges().isChanged("noOfDays");

                if (!relevantFieldChanged) {
                    System.out.println("No relevant fields changed, skipping recalculation");
                    return;
                }
            }

            // Only recalculate if we're creating a new type or changed relevant fields
            leaveCalculationService.recalculateLeavesForAllUsers();
        } else {
            System.out.println("Non-significant LeaveType event detected: " + changeType);
        }
    }

    // Keep this method for logging purposes
    @EventListener
    public void onAnyLeaveTypeEvent(EntityChangedEvent<LeaveType> event) {
        System.out.println("Any LeaveType event detected: " + event.getType());
    }
}