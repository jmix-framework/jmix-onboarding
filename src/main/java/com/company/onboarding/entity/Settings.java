package com.company.onboarding.entity;

import io.jmix.appsettings.entity.AppSettingsEntity;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@JmixEntity
@Table(name = "SETTINGS", indexes = {
        @Index(name = "IDX_SETTINGS_ENTITY_PARAM", columnList = "ENTITY_PARAM_ID")
})
@Entity
public class Settings extends AppSettingsEntity {
    @JmixGeneratedValue
    @Column(name = "UUID")
    private UUID uuid;

    @Column(name = "STRING_PARAM")
    private String stringParam;

    @Column(name = "DATE_PARAM")
    private LocalDate dateParam;

    @JoinColumn(name = "ENTITY_PARAM_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Department entityParam;

    public Department getEntityParam() {
        return entityParam;
    }

    public void setEntityParam(Department entityParam) {
        this.entityParam = entityParam;
    }

    public LocalDate getDateParam() {
        return dateParam;
    }

    public void setDateParam(LocalDate dateParam) {
        this.dateParam = dateParam;
    }

    public String getStringParam() {
        return stringParam;
    }

    public void setStringParam(String stringParam) {
        this.stringParam = stringParam;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}