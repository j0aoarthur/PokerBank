package com.j0aoarthur.pokerbank.infra.context;

import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Account;

public interface AuthContextService {
    Account getCurrentUser();
    Club getCurrentClub();
}
