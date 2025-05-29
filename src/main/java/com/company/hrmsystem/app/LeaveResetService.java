package com.company.hrmsystem.service;

import com.company.hrmsystem.entity.User;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class LeaveResetService {

    @Autowired
    private DataManager dataManager;

    @Scheduled(cron = "0 1 0 1 1 *") // Run at 00:01 AM on January 1st
    @Transactional
    public void resetAnnualLeaves() {
        List<User> users = dataManager.load(User.class)
                .all()
                .list();

        for (User user : users) {
            // Use the refreshTotalLeaves method instead of fixed constant
            user.refreshTotalLeaves();
            dataManager.save(user);
        }
    }
}