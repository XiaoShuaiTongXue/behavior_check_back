package com.behavior.utils;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static final String regEx = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";


    public static boolean isEmpty(String str){
        return str == null || str.equals("");
    }

    public static boolean isEmailAddressOk(String emailAddress){
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(emailAddress);
        return m.matches();
    }

    public static boolean judgeSex(String sex){
        if (sex.equals(Constants.User.DEFAULT_SEX_FEMALE) || sex.equals(Constants.User.DEFAULT_SEX_MALE)) {
            return true;
        }
        return false;
    }

    public static boolean judgeState(int state){
        if (Constants.SignState.TRUANT_STATE == state){
            return true;
        }else if (Constants.SignState.LATE_STATE == state){
            return true;
        }else if (Constants.SignState.NORMAL_STATE == state){
            return true;
        } else if (Constants.SignState.VACATION_STATE == state){
            return true;
        }else {
            return false;
        }
    }

    public static Date formatDate(String date) throws ParseException {
        return DateUtils.parseDate(date, "yyyy-MM-dd HH:mm:ss");
    }

}
