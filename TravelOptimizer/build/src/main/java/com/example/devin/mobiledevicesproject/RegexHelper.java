package com.example.devin.mobiledevicesproject;

class RegexHelper {
    final String email = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{}|~-]+@\\w+\\.\\w+$";
    final String password = "^(?=.*[a-z]+)(?=.*[A-Z])(?=.*\\d).{8,}$";
    final String birthdateDay = "^[\\d]{1,2}$";
    final String birthdateMonth = "^[\\d]{1,2}$";
    final String birthdateYear = "^[\\d]{4}$";
    final String birthdate = "^[\\d]{1,2}\\/[\\d]{1,2}\\/[\\d]{4}$";
    final String licenseClass = "^[1-7]$";
    final String gender = "^[mM|fF]$";
}