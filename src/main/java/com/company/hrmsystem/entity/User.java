package com.company.hrmsystem.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.HasTimeZone;
import io.jmix.core.annotation.Secret;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.entity.annotation.SystemLevel;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.security.authentication.JmixUserDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@JmixEntity
@Entity
@Table(name = "USER_", indexes = {
        @Index(name = "IDX_USER__ON_USERNAME", columnList = "USERNAME", unique = true),
        @Index(name = "IDX_USER__MANAGER", columnList = "MANAGER_ID")
})
public class User implements JmixUserDetails, HasTimeZone {

    @Id
    @Column(name = "ID", nullable = false)
    @JmixGeneratedValue
    private UUID id;

    @Column(name = "TOTAL_LEAVES")
    private Integer totalLeaves;

    @PastOrPresent
    @NotNull
    @Column(name = "JOINED_DATE", nullable = false)
    private LocalDate joinedDate;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "MANAGER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User manager;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version = 0;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Secret
    @SystemLevel
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Email
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ACTIVE")
    private Boolean active = true;

    @Column(name = "TIME_ZONE_ID")
    private String timeZoneId;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;


    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    public Integer getTotalLeaves() {
        return totalLeaves;
    }

    public void setTotalLeaves(Integer totalLeaves) {
        this.totalLeaves = totalLeaves;
    }
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : Collections.emptyList();
    }

    @Override
    public void setAuthorities(final Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }

    @InstanceName
    @DependsOnProperties({"firstName", "lastName", "username"})
    public String getDisplayName() {
        return String.format("%s %s [%s]", (firstName != null ? firstName : ""),
                (lastName != null ? lastName : ""), username).trim();
    }

    @Override
    public String getTimeZoneId() {
        return timeZoneId;
    }

    @Override
    public boolean isAutoTimeZone() {
        return true;
    }

    public void setTimeZoneId(final String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    /**
     * Calculates the total leaves a user is eligible for based on their join date.
     * Users get 36 leaves per year (3 per month) pro-rated for the remaining months of the join year.
     */
    public void calculateInitialLeaves() {
        if (joinedDate == null) {
            return;
        }

        // Default annual leave allowance is 36 days (3 per month)
        final int ANNUAL_LEAVE_DAYS = 36;
        final int MONTHS_IN_YEAR = 12;
        final double LEAVES_PER_MONTH = (double) ANNUAL_LEAVE_DAYS / MONTHS_IN_YEAR;

        LocalDate currentDate = LocalDate.now();

        // If joined in current year, calculate pro-rated leaves
        if (joinedDate.getYear() == currentDate.getYear()) {
            // Get the month after joining (1-based, January is 1)
            int joiningMonth = joinedDate.getMonthValue();

            // Calculate remaining months in the year (excluding join month)
            int remainingMonths = MONTHS_IN_YEAR - joiningMonth;

            // Calculate pro-rated leaves
            int proRatedLeaves = (int) Math.round(remainingMonths * LEAVES_PER_MONTH);

            // Set the calculated leaves
            setTotalLeaves(proRatedLeaves);
        } else {
            // If joined in previous years, set full annual leave
            setTotalLeaves(ANNUAL_LEAVE_DAYS);
        }
    }

    public User() {
        // Default constructor
    }

    @PostPersist
    @PostLoad
    public void initializeLeaves() {
        // If totalLeaves is not set, calculate it
        if (totalLeaves == null) {
            calculateInitialLeaves();
        }
    }
}