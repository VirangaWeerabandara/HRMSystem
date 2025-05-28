package com.company.hrmsystem.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum LeaveStatus implements EnumClass<String> {

    PENDING("A"),
    APPROVED("B"),
    REJECTED("C");

    private final String id;

    LeaveStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static LeaveStatus fromId(String id) {
        for (LeaveStatus at : LeaveStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}