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
        System.out.println("LeaveType change detected: " + event.getType());
        leaveCalculationService.recalculateLeavesForAllUsers();
    }

    // Remove the threaded approach as it can cause issues
    @EventListener
    public void onAnyLeaveTypeEvent(EntityChangedEvent<LeaveType> event) {
        System.out.println("Any LeaveType event detected: " + event.getType());
    }
}