package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.MemberStats;

import java.util.List;

public interface MemberStatsService {
    void updateMemberStats(GameParticipant gameParticipant);
    List<MemberStats> getAllMemberStats();
    List<MemberStats> getTopMembers();
}
