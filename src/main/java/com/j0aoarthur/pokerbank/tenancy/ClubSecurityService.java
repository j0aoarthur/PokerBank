package com.j0aoarthur.pokerbank.tenancy;

import com.j0aoarthur.pokerbank.infra.context.ClubContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("clubSecurityService")
@RequiredArgsConstructor
public class ClubSecurityService {

    public boolean hasClubContext() {
        return ClubContext.getCurrentClubId() != null;
    }
}
