package com.example.eas.settings;

public class Validation {

        public static  String vehicleno="^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$";
        public  static String email="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        public  static String mobile="^(((\\+44\\s?\\d{4}|\\(?0\\d{4}\\)?)\\s?\\d{3}\\s?\\d{3})|((\\+44\\s?\\d{3}|\\(?0\\d{3}\\)?)\\s?\\d{3}\\s?\\d{4})|((\\+44\\s?\\d{2}|\\(?0\\d{2}\\)?)\\s?\\d{4}\\s?\\d{4}))(\\s?\\#(\\d{4}|\\d{3}))?$";
        public static String text="[a-z A-Z\\\\s]+";
        public static String digit=  "\\\\d+";
//    Matches     +447222555555   | +44 7222 555 555 | (0722) 5555555 #2222
//    Non-Matches (+447222)555555 | +44(7222)555555  | (0722) 5555555 #22


}
