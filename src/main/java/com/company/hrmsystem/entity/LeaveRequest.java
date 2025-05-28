package com.company.hrmsystem.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@JmixEntity
@Table(name = "LEAVE_REQUEST", indexes = {
        @Index(name = "IDX_LEAVE_REQUEST_USER", columnList = "USER_ID"),
        @Index(name = "IDX_LEAVE_REQUEST_APPROVER", columnList = "APPROVER_ID")
})
@Entity
public class LeaveRequest {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "USER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @JoinColumn(name = "APPROVER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User approver;

    @Future
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Future
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;

    @Column(name = "REASON", nullable = false)
    @Lob
    @NotNull
    private String reason;

    @Column(name = "LEAVE_STATUS", nullable = false)
    @NotNull
    private String leaveStatus;

    @JoinColumn(name = "LEAVE_TYPE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private LeaveType leaveType;

    // Add getters and setters
    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LeaveStatus getLeaveStatus() {
        return leaveStatus == null ? null : LeaveStatus.fromId(leaveStatus);
    }

    public void setLeaveStatus(LeaveStatus leaveStatus) {
        this.leaveStatus = leaveStatus == null ? null : leaveStatus.getId();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}