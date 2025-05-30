package com.company.hrmsystem.security;

import com.company.hrmsystem.entity.LeaveRequest;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "ManagerRole", code = ManagerRole.CODE)
public interface ManagerRole {
    String CODE = "manager-role";

    @EntityAttributePolicy(entityClass = LeaveRequest.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = LeaveRequest.class, actions = EntityPolicyAction.ALL)
    void leaveRequest();

    @MenuPolicy(menuIds = {"LeaveRequest.list", "CreateLeaveRequest.list"})
    @ViewPolicy(viewIds = {"LeaveRequest.list", "CreateLeaveRequest.list", "LeaveRequest.detail"})
    void screens();
}