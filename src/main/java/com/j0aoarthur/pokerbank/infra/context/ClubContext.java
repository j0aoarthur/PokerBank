package com.j0aoarthur.pokerbank.infra.context;

public final class ClubContext {

    private static final ThreadLocal<Long> currentClubId = new ThreadLocal<>();

    private ClubContext() {
    }

    public static void setCurrentClubId(Long clubId) {
        currentClubId.set(clubId);
    }

    public static Long getCurrentClubId() {
        return currentClubId.get();
    }

    public static void clear() {
        currentClubId.remove();
    }
}
