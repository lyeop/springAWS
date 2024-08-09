package com.example.Spring_shop.util;

import com.example.Spring_shop.constant.Notice;
import com.example.Spring_shop.constant.Role;

public class RoleToNoticeConverter {

    public static Notice convertRoleToNotice(Role role) {
        if (role == Role.ADMIN) {
            return Notice.ADMIN;
        } else if (role == Role.USER) {
            return Notice.USER;
        } else {
            throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}