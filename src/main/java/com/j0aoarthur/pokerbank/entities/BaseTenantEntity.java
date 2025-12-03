package com.j0aoarthur.pokerbank.entities;

import com.j0aoarthur.pokerbank.infra.clubTenancy.ClubFilterable;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@FilterDef(name = "clubFilter", parameters = {@ParamDef(name = "clubId", type = Long.class)}, defaultCondition = "club_id = :clubId")
@Filter(name = "clubFilter", condition = "club_id = :clubId")
public class BaseTenantEntity implements ClubFilterable {

    @Column(name = "club_id", updatable = false)
    private Long clubId;

    @Override
    public Long getClubId() {
        return clubId;
    }

    @Override
    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }
}
