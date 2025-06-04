package com.company.hrmsystem.service;

import com.company.hrmsystem.entity.LeaveRequest;
import com.company.hrmsystem.entity.LeaveStatus;
import com.company.hrmsystem.entity.User;
import com.company.hrmsystem.entity.UserLeaveStats;
import com.vaadin.flow.component.dialog.Dialog;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserLeaveStatsService {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private UserLeaveSummaryService userLeaveSummaryService;

    public UserLeaveStats getUserLeaveStats(UUID userId) {
        // Load user with all necessary data
        User user = dataManager.load(User.class)
                .id(userId)
                .one();

        // Create and populate the DTO
        UserLeaveStats dto = new UserLeaveStats(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getTotalLeaves()
        );

        // Set available leaves
        dto.setAvailableLeaves(userLeaveSummaryService.getAvailableLeaveForUser(user));

        // Set prorated leaves
        dto.setProratedLeaves(userLeaveSummaryService.getProratedAvailableLeavesForCurrentUser(user));

        // Get consumed leaves
        Map<String, Integer> consumedLeaves = calculateConsumedLeaves(user);
        dto.setConsumedLeaves(consumedLeaves);

        // Get pending leaves
        Map<String, Integer> pendingLeaves = calculatePendingLeaves(user);
        dto.setPendingLeaves(pendingLeaves);

        return dto;
    }

    private Map<String, Integer> calculateConsumedLeaves(User user) {
        Map<String, Integer> consumedLeaves = new HashMap<>();

        List<LeaveRequest> approvedRequests = dataManager.load(LeaveRequest.class)
                .query("select e from LeaveRequest e where e.user = :user and e.leaveStatus = :status")
                .parameter("user", user)
                .parameter("status", LeaveStatus.APPROVED.getId())
                .list();

        for (LeaveRequest request : approvedRequests) {
            String leaveType = request.getLeaveType().getName();
            Integer workingDays = request.getWorkingDays() != null ? request.getWorkingDays() : 0;

            consumedLeaves.put(leaveType,
                    consumedLeaves.getOrDefault(leaveType, 0) + workingDays);
        }

        return consumedLeaves;
    }

    private Map<String, Integer> calculatePendingLeaves(User user) {
        Map<String, Integer> pendingLeaves = new HashMap<>();

        List<LeaveRequest> pendingRequests = dataManager.load(LeaveRequest.class)
                .query("select e from LeaveRequest e where e.user = :user and e.leaveStatus = :status")
                .parameter("user", user)
                .parameter("status", LeaveStatus.PENDING.getId())
                .list();

        for (LeaveRequest request : pendingRequests) {
            String leaveType = request.getLeaveType().getName();
            Integer workingDays = request.getWorkingDays() != null ? request.getWorkingDays() : 0;

            pendingLeaves.put(leaveType,
                    pendingLeaves.getOrDefault(leaveType, 0) + workingDays);
        }

        return pendingLeaves;
    }

}