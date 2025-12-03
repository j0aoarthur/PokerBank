package com.j0aoarthur.pokerbank.infra.context;

import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.User;

public interface AuthContextService {
    User getCurrentUser();
    Club getCurrentClub();
}
