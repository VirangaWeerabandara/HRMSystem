package com.company.hrmsystem.service;

import com.company.hrmsystem.entity.Holiday;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveRequestService {

    @Autowired
    private DataManager dataManager;


    public int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return 0;
        }

        List<LocalDate> holidays = loadHolidaysBetweenDates(startDate, endDate);

        int workingDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (!isWeekend(currentDate) && !holidays.contains(currentDate)) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return workingDays;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private List<LocalDate> loadHolidaysBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Holiday> holidays = dataManager.load(Holiday.class)
                .query("select e from Holiday e where e.holidayDate >= :startDate and e.holidayDate <= :endDate")
                .parameter("startDate", startDate)
                .parameter("endDate", endDate)
                .list();

        return holidays.stream()
                .map(Holiday::getHolidayDate)
                .collect(Collectors.toList());
    }
}