package com.company.hrmsystem.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Entity;

import java.util.Map;
import java.util.UUID;

@JmixEntity
public class UserLeaveStats {
    @JmixGeneratedValue
    @JmixId
    private UUID id;

    private String username;
    private Integer totalLeaves;
    private Map<String, Integer> availableLeaves;
    private Map<String, Integer> proratedLeaves;
    private Map<String, Integer> consumedLeaves;
    private Map<String, Integer> pendingLeaves;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserLeaveStats() {
    }

    public UserLeaveStats(UUID userId, String username, String fullName,
                             Integer totalLeaves) {
        this.username = username;
        this.totalLeaves = totalLeaves;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getTotalLeaves() {
        return totalLeaves;
    }

    public void setTotalLeaves(Integer totalLeaves) {
        this.totalLeaves = totalLeaves;
    }

    public Map<String, Integer> getAvailableLeaves() {
        return availableLeaves;
    }

    public void setAvailableLeaves(Map<String, Integer> availableLeaves) {
        this.availableLeaves = availableLeaves;
    }

    public Map<String, Integer> getProratedLeaves() {
        return proratedLeaves;
    }

    public void setProratedLeaves(Map<String, Integer> proratedLeaves) {
        this.proratedLeaves = proratedLeaves;
    }

    public Map<String, Integer> getConsumedLeaves() {
        return consumedLeaves;
    }

    public void setConsumedLeaves(Map<String, Integer> consumedLeaves) {
        this.consumedLeaves = consumedLeaves;
    }

    public Map<String, Integer> getPendingLeaves() {
        return pendingLeaves;
    }

    public void setPendingLeaves(Map<String, Integer> pendingLeaves) {
        this.pendingLeaves = pendingLeaves;
    }

}