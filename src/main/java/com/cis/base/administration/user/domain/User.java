/**
 * Copyright 2023 CIS (Cam info Services)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cis.base.administration.user.domain;

import com.cis.base.administration.office.domain.Office;
import com.cis.base.administration.permission.domain.Permission;
import com.cis.base.administration.role.domain.Role;
import com.cis.base.config.core.security.domain.PlatformUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author YSivlay
 */
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}, name = "username_org"),
        @UniqueConstraint(columnNames = {"phone"}, name = "phone"),
        @UniqueConstraint(columnNames = {"email"}, name = "email")
})
public class User extends AbstractPersistable<Long> implements PlatformUser {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "fullname", nullable = false, length = 100)
    private String fullname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "lat", precision = 10, scale = 8)
    private BigDecimal lat;

    @Column(name = "lng", precision = 11, scale = 8)
    private BigDecimal lng;

    @Column(name = "address")
    private String address;

    @Column(name = "firsttime_login_remaining", nullable = false)
    private boolean firstTimeLoginRemaining;

    @Column(name = "nonexpired", nullable = false)
    private boolean accountNonExpired;

    @Column(name = "nonlocked", nullable = false)
    private final boolean accountNonLocked;

    @Column(name = "nonexpired_credentials", nullable = false)
    private final boolean credentialsNonExpired;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Temporal(TemporalType.DATE)
    @Column(name = "last_time_password_updated")
    private Date lastTimePasswordUpdated;

    @Column(name = "password_never_expires", nullable = false)
    private boolean passwordNeverExpires;

    @Column(name = "is_self_service_user", nullable = false)
    private boolean isSelfServiceUser;

    @Column(name = "attempts")
    private Long attempts;

    @Column(name = "phone")
    private String phone;

    @Column(name = "lang")
    private String lang;

    @Column(name = "is_term_condition")
    private Boolean isTermCondition;

    @Column(name = "from_sign_up", nullable = false)
    private boolean fromSignUp;

    @Column(name = "activated")
    private Date activated;

    @Column(name = "activation_key")
    private String activationKey;

    @Column(name = "last_sent_email_date")
    private Date lastSentEmailDate;

    @Column(name = "last_sent_sms_date")
    private Date lastSentSmsDate;

    @Column(name = "sms_attempts")
    private Long smsAttempts;

    @Column(name = "note")
    private String note;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "createdby_id")
    private Long createdBy;

    @Column(name = "lastmodified_date")
    private Date lastUpdatedDate;

    @Column(name = "lastmodifiedby_id")
    private Long lastUpdatedBy;

    protected User() {
        this.accountNonLocked = false;
        this.credentialsNonExpired = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return populateGrantedAuthorities();
    }

    private List<GrantedAuthority> populateGrantedAuthorities() {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (final Role role : this.roles) {
            final Collection<Permission> permissions = role.getPermissions();
            for (final Permission permission : permissions) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getCode()));
            }
        }
        return grantedAuthorities;
    }
}
