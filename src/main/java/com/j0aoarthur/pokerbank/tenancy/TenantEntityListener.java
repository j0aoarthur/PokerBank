package com.j0aoarthur.pokerbank.tenancy;

import com.j0aoarthur.pokerbank.entities.BaseTenantEntity;
import com.j0aoarthur.pokerbank.infra.context.ClubContext;
import jakarta.persistence.PrePersist;

public class TenantEntityListener {

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof BaseTenantEntity baseEntity) {
            Long clubId = ClubContext.getCurrentClubId();
            if (clubId != null && baseEntity.getClubId() == null) {
                baseEntity.setClubId(clubId);
            }
        }
    }
}
