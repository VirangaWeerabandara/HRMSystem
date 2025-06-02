package com.company.hrmsystem.service;

import com.company.hrmsystem.entity.LeaveType;
import com.company.hrmsystem.entity.User;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import io.jmix.core.FetchPlan;

import java.util.List;

@Service
public class LeaveCalculationService {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private ApplicationContext applicationContext;

    private void injectDependencies(User user) {
        try {
            java.lang.reflect.Field dataManagerField = User.class.getDeclaredField("dataManager");
            dataManagerField.setAccessible(true);
            dataManagerField.set(user, dataManager);

            java.lang.reflect.Field appContextField = User.class.getDeclaredField("applicationContext");
            appContextField.setAccessible(true);
            appContextField.set(user, applicationContext);
        } catch (Exception e) {
            System.err.println("Error injecting dependencies: " + e.getMessage());
        }
    }

    private static int totalActiveLeaveDays = 0;

    public static int getStaticTotalLeaveDays() {
        return totalActiveLeaveDays > 0 ? totalActiveLeaveDays : 36; // Default to 36 if not yet calculated
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recalculateLeavesForAllUsers() {
        try {
            System.out.println("Recalculating leaves for all users");

            List<LeaveType> activeLeaveTypes = dataManager.load(LeaveType.class)
                    .query("select e from LeaveType e where e.active = true")
                    .fetchPlan(FetchPlan.BASE)
                    .list();

            totalActiveLeaveDays = activeLeaveTypes.stream()
                    .mapToInt(LeaveType::getNoOfDays)
                    .sum();

            System.out.println("Total active leave days: " + totalActiveLeaveDays);

            List<User> users = dataManager.load(User.class)
                    .all()
                    .list();

            System.out.println("Found " + users.size() + " users to update");

            boolean noActiveLeaveTypes = activeLeaveTypes.isEmpty();

            for (User user : users) {
                if (user.getJoinedDate() != null) {
                    injectDependencies(user);

                    if (noActiveLeaveTypes) {
                        user.fallbackCalculateLeaves();
                        System.out.println("Using fallback calculation for user: " + user.getUsername());
                    } else {
                        user.calculateInitialLeaves();
                    }

                    // Save the updated user
                    dataManager.save(user);
                    System.out.println("Updated leaves for user: " + user.getUsername() +
                            " to " + user.getTotalLeaves() + " days");
                }
            }
        } catch (Exception e) {
            System.err.println("Error in recalculating leaves: " + e.getMessage());
            e.printStackTrace();
        }
    }
}