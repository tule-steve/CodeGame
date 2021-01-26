package com.codegame.anticorrupt;

import com.codegame.security.out.GetUserDetail;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailAdapter implements GetUserDetail {

    @Override
    public UserDetails loadUserByEmail(String email) {
        return null;
    }
}
