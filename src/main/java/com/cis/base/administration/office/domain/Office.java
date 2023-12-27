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
package com.cis.base.administration.office.domain;

import com.cis.base.config.core.json.JsonCommand;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.*;

/**
 * @author YSivlay
 */
@Getter
@Setter
@Entity
@Table(name = "offices", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "name_org")})
public class Office extends AbstractPersistable<Long> {

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private final List<Office> children = new LinkedList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Office parent;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "hierarchy", nullable = true, length = 50)
    private String hierarchy;

    @Column(name = "opening_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date openingDate;

    @Column(name = "external_id", length = 100)
    private String externalId;

    protected Office() {
    }

    private Office(final Office parent, final String name, final LocalDate openingDate, final String externalId) {
        this.parent = parent;
        this.openingDate = openingDate.toDateTimeAtStartOfDay().toDate();
        if (parent != null) {
            this.parent.addChild(this);
        }
        if (StringUtils.isNotBlank(name)) {
            this.name = name.trim();
        } else {
            this.name = null;
        }
        if (StringUtils.isNotBlank(externalId)) {
            this.externalId = externalId.trim();
        } else {
            this.externalId = null;
        }
    }

    private void addChild(Office office) {
        this.children.add(office);
    }

    /**
     * Get existing params from json RESTFull API.
     *
     * @param command
     * @return {@link Office}
     */
    public static Office fromJson(final JsonCommand command) {

        return new Office();
    }

    /**
     * Get existing params from json RESTFull API.
     *
     * @param command
     * @return {@link Map}
     */
    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        return actualChanges;
    }
}