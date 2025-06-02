package com.company.hrmsystem.security;

import com.company.hrmsystem.entity.Holiday;
import com.company.hrmsystem.entity.LeaveRequest;
import com.company.hrmsystem.entity.LeaveType;
import com.company.hrmsystem.entity.User;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "employee", code = EmployeeRole.CODE)
public interface EmployeeRole {
    String CODE = "employee";

    @EntityAttributePolicy(entityClass = LeaveRequest.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = LeaveRequest.class, actions = EntityPolicyAction.ALL)
    void leaveRequest();

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.READ)
    void user();

    @EntityAttributePolicy(entityClass = LeaveType.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = LeaveType.class, actions = EntityPolicyAction.READ)
    void leaveType();

    @EntityAttributePolicy(entityClass = Holiday.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Holiday.class, actions = EntityPolicyAction.READ)
    void holiday();

    @MenuPolicy(menuIds = "CreateLeaveRequest.list")
    @ViewPolicy(viewIds = {"CreateLeaveRequest.list", "LeaveRequest.detail", "LeaveType.list"})
    void screens();
}