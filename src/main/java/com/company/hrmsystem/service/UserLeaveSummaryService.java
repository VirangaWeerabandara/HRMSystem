package com.company.hrmsystem.service;

import com.company.hrmsystem.entity.LeaveType;
import com.company.hrmsystem.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public Map<String, Integer> getAvailableLeaveForUser(User user) {
        Map<String, Integer> totalPerType = getLeaveTypeCountsForUser(user);

        List<KeyValueEntity> taken = dataManager.loadValues(
                        "select lr.leaveType.name as leaveTypeName, sum(lr.workingDays) as totalDays " +
                                "from LeaveRequest lr where lr.user = :user and lr.leaveStatus = 'APPROVED' " +
                                "group by lr.leaveType.name"
                )
                .parameter("user", user)
                .properties("leaveTypeName", "totalDays")
                .list();

        Map<String, Integer> takenPerType = new HashMap<>();
        for (KeyValueEntity row : taken) {
            String leaveTypeName = row.getValue("leaveTypeName");
            Number totalDays = row.getValue("totalDays");
            takenPerType.put(leaveTypeName, totalDays != null ? totalDays.intValue() : 0);
        }

        Map<String, Integer> available = new HashMap<>();
        for (Map.Entry<String, Integer> entry : totalPerType.entrySet()) {
            int takenDays = takenPerType.getOrDefault(entry.getKey(), 0);
            available.put(entry.getKey(), entry.getValue() - takenDays);
        }
        return available;
    }

    public Map<String, Integer> getProratedAvailableLeavesForCurrentUser(User user) {
        Map<String, Integer> availableLeaves = new HashMap<>();
        LocalDate now = LocalDate.now();

        List<LeaveType> leaveTypes = dataManager.load(LeaveType.class)
                .query("select e from LeaveType e where e.active = true")
                .list();

        for (LeaveType leaveType : leaveTypes) {
            if (Boolean.TRUE.equals(leaveType.getProrated())) {
                LocalDate accrualStart = user.getJoinedDate().isAfter(now.withDayOfYear(1))
                        ? user.getJoinedDate()
                        : now.withDayOfYear(1);
                int months = accrualStart.until(now.withDayOfMonth(1)).getMonths() + 1;
                months = Math.max(months, 0);

                int annual = leaveType.getNoOfDays();
                int monthlyAccrual = annual / 12;
                int maxNegative = 14 - annual; // e.g., 14 allowed, 12 annual, so -2

                int accrued = months * monthlyAccrual;

                // Leaves taken
                Integer taken = dataManager.loadValue(
                                "select sum(e.workingDays) from LeaveRequest e where e.user = :user and e.leaveType = :leaveType and e.leaveStatus = 'APPROVED'",
                                Integer.class)
                        .parameter("user", user)
                        .parameter("leaveType", leaveType)
                        .one();
                if (taken == null) taken = 0;

                int available = accrued - taken;
                // Allow negative up to maxNegative
                available = Math.max(available, maxNegative * -1);

                availableLeaves.put(leaveType.getName(), available);
            }
        }
        return availableLeaves;
    }
}