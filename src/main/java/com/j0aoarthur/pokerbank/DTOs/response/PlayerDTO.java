package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.Player;
import com.j0aoarthur.pokerbank.entities.Role;

public record PlayerDTO(Long id, String name, Role role) {
    public PlayerDTO(Player player) {
        this(player.getId(), player.getName(), player.getRole());
    }
}
