package com.ryms.mathagoras.Configurations;

public class Config {
    public static final String BASE_URL = "https://mathagoras-backend.herokuapp.com";

    public static final String TEACHER_ROUTE = BASE_URL+"/teacher";
    public static final String TEACHER_SIGNUP = TEACHER_ROUTE+"/signup";

    public static final String STUDENT_ROUTE = BASE_URL+"/student";
    public static final String STUDENT_LOGIN = STUDENT_ROUTE+"/login";
    public static final String STUDENT_SIGNUP = STUDENT_ROUTE+"/signup";

    public static final String GET_COURSES = BASE_URL+"/courseStudent";
}
