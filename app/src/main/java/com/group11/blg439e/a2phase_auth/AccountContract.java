package com.group11.blg439e.a2phase_auth;

import android.provider.BaseColumns;

public final class AccountContract {
    private AccountContract(){};
    public static class Account implements BaseColumns{
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }
}
