package com.behavior.utils;

public interface Constants {

    interface User {
        String DEFAULT_AVATAR = "https://cdn.sunofbeaches.com/images/default_avatar.png";
        String DEFAULT_SEX_MALE = "男";
        String DEFAULT_SEX_FEMALE = "女";
        String STUDENT_KEY_TOKEN = "student_key_token";
        String STUDENT_COOKIE_TOKEN_KEY = "student_cookie_token_key";
        String TEACHER_KEY_TOKEN = "teacher_key_token";
        String TEACHER_COOKIE_TOKEN_KEY = "teacher_cookie_token_key";
        String TEACHER_SIGN = "teacher_sign";
        String NORMAL_SIGN = "normal_sign";
        String LATER_SIGN = "later_sign";
        String TEACHER_KEY_ONLINE = "teacher_key_online";
        String BEHAVIOR_KEY_ONLINE = "behavior_key_online";
        String BEHAVIOR_KEY_OUTLINE = "behavior_key_outline";
        String CLASS_KEY = "class_key";
    }

    interface Behavior{
        int BEHAVIOR_ONLINE = 1;
        int BEHAVIOR_OUTLINE = 2;
    }

    /**
     * 旷课：0
     * 迟到：1
     * 正常签到：2
     * 请假：3
     */
    interface SignState {
        int TRUANT_STATE = 0;
        int LATE_STATE = 1;
        int NORMAL_STATE = 2;
        int VACATION_STATE = 3;
    }

    interface TimeValueByMS {
        int S = 1000;
        int MIN = S * 60;
        int MIN_3 = MIN * 3;
        int  MIN_5 = 5 * MIN;
        int HOUR = MIN * 60;
        int HOUR_2 = 2 * HOUR;
        int DAY = HOUR * 24;
        int WEEK = DAY * 7;
        int MONTH = 30 * DAY;
    }

    interface TimeValueByS {
        int MIN = 60;
        int HOUR = MIN * 60;
        int HOUR_2 = 2 * HOUR;
        int DAY = HOUR * 24;
        int WEEK = DAY * 7;
        int MONTH = 30 * DAY;
    }

    interface Page {
        int DEFAULT_PAGE = 1;
        int DEFAULT_SIZE = 5;
    }
}
