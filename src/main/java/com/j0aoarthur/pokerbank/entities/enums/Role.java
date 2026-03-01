package com.j0aoarthur.pokerbank.entities.enums;

public enum Role {
    OWNER,
    ADMIN,
    PLAYER;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
