package com.company.hrmsystem.service;

import com.company.hrmsystem.entity.LeaveType;
import com.company.hrmsystem.entity.User;
import io.jmix.core.DataManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserLeaveSummaryService {

    private final DataManager dataManager;

    public UserLeaveSummaryService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public Map<String, Integer> getLeaveTypeCountsForUser(User user) {
        List<LeaveType> leaveTypes = dataManager.load(LeaveType.class)
                .query("select e from LeaveType e where e.active = true")
                .list();

        int totalLeaveDays = leaveTypes.stream()
                .mapToInt(LeaveType::getNoOfDays)
                .sum();

        int userTotalLeaves = user.getTotalLeaves() != null ? user.getTotalLeaves() : 0;

        Map<String, Integer> result = new HashMap<>();
        for (LeaveType leaveType : leaveTypes) {
            int count = (int) Math.round(
                    ((double) userTotalLeaves * leaveType.getNoOfDays()) / totalLeaveDays
            );
            result.put(leaveType.getName(), count);
        }
        return result;
    }
}