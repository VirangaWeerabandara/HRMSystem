package com.company.hrmsystem.security;

import com.company.hrmsystem.entity.LeaveRequest;
import com.company.hrmsystem.entity.User;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "MangerRole", code = MangerRole.CODE)
public interface MangerRole {
    String CODE = "manger-role";

    @EntityAttributePolicy(entityClass = LeaveRequest.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = LeaveRequest.class, actions = EntityPolicyAction.ALL)
    void leaveRequest();

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.READ)
    void user();

    @MenuPolicy(menuIds = {"LeaveRequest.list", "User.list", "CreateLeaveRequest.list"})
    @ViewPolicy(viewIds = {"CreateLeaveRequest.list", "LeaveRequest.list", "LeaveRequest.detail", "User.detail", "User.list"})
    void screens();

}