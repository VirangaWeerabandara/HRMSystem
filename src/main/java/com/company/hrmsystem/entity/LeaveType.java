package com.company.hrmsystem.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JmixEntity
@Table(name = "LEAVE_TYPE")
@Entity
public class LeaveType {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "NAME", nullable = false, unique = true)
    @NotNull
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Min(0)
    @Column(name = "DEFAULT_DAYS", nullable = false)
    @NotNull
    private Integer defaultDays;

    @Column(name = "ACTIVE", nullable = false)
    @NotNull
    private Boolean active = true;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDefaultDays() {
        return defaultDays;
    }

    public void setDefaultDays(Integer defaultDays) {
        this.defaultDays = defaultDays;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}