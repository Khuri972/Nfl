package com.Nflicks.custom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CRAFT BOX on 3/1/2017.
 */

public class Validation {

    public static int EMAIL=1;
    public static int BLANK_CHECK=2;
    public static int PASSWORD=3;
    public static int MOBILE=4;
    public static int ADDRESS=10;
    public static int UKMOBILE=6;
    public static int IPV4=7;
    public static int WEBSITE=8;



    public static boolean isValid(int type,String value)
    {

        switch (type)
        {
            case 1:
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();

            case 2:
                return (value.trim().equals(""))?false:true;
            case 3:
                if(value.trim().length()<6)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            case 4:
                if(value.trim().length()<10)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            case 5:
                if(value.trim().length()<10)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            case 6:
                String regexStr = "^(\\+44\\s?7\\d{3}|\\(?07\\d{3}\\)?)\\s?\\d{3}\\s?\\d{3}$";
                 pattern = Pattern.compile(regexStr);
                 matcher = pattern.matcher(value);
                return matcher.matches();
            case 7:
                if (value.indexOf(".")>0)
                {
                    String ipv4 =   "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
                    pattern = Pattern.compile(ipv4);
                    matcher = pattern.matcher(value);
                    return matcher.matches();
                }
                else
                {
                    return false;
                }
            case 8:
                int counter = 0;
                for( int i=0; i<value.length(); i++ ) {
                    if( value.charAt(i) == '.' ) {
                        counter++;
                    }
                }

                if(counter<2)
                {
                    return false;
                }
                else
                {
                    return true;
                }

            default:
                return false;
        }
    }
}
