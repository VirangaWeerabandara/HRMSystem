package com.company.hrmsystem.app;

import com.company.hrmsystem.entity.LeaveType;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class LeaveTypeInitializer {

    @Autowired
    private DataManager dataManager;

    @EventListener
    @Transactional
    public void onApplicationStarted(ApplicationStartedEvent event) {
        // Check if leave types already exist
        List<LeaveType> existingTypes = dataManager.load(LeaveType.class)
                .all()
                .list();

        if (existingTypes.isEmpty()) {
            // Create and save sick leave type
            LeaveType sickLeave = createLeaveType("Sick",
                    "Leave taken due to illness or medical appointments", 12, true);
            dataManager.save(sickLeave);

            // Create and save casual leave type
            LeaveType casualLeave = createLeaveType("Casual",
                    "Leave for personal matters and short breaks", 12, true);
            dataManager.save(casualLeave);
        }
    }

    private LeaveType createLeaveType(String name, String description, Integer defaultDays, Boolean active) {
        LeaveType leaveType = dataManager.create(LeaveType.class);
        leaveType.setName(name);
        leaveType.setDescription(description);
        leaveType.setDefaultDays(defaultDays);
        leaveType.setActive(active);
        return leaveType;
    }
}