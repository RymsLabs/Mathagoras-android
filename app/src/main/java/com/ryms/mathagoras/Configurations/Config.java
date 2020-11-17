package com.ryms.mathagoras.Configurations;

public class Config {
    public static final String BASE_URL = "https://mathagoras-backend.herokuapp.com";

    public static final String TEACHER_ROUTE = BASE_URL+"/teacher";
    public static final String TEACHER_SIGNUP = TEACHER_ROUTE+"/signup";
    public static final String TEACHER_LOGIN = TEACHER_ROUTE +"/login";
    public static final String ADD_COURSES = BASE_URL+"/course";
    public static final String GET_COURSES_TEACHER = BASE_URL+"/course/teacher/all";
    public static final String CREATE_DISCUSSION = BASE_URL+"/discussion/";
    public static final String SEND_MESSAGE_TEACHER = BASE_URL+"/messages/teacher/";
    public static final String CREATE_POST = BASE_URL+"/post/";

    public static final String STUDENT_ROUTE = BASE_URL+"/student";
    public static final String STUDENT_LOGIN = STUDENT_ROUTE+"/login";
    public static final String STUDENT_SIGNUP = STUDENT_ROUTE+"/signup";
    public static final String GET_COURSES = BASE_URL+"/courseStudent";
    public static final String ENROLL_COURSES = BASE_URL+"/courseStudent/enroll/";
    public static final String SEND_MESSAGE_STUDENT = BASE_URL+"/messages/student/";

    public static final String GET_CLASS = BASE_URL+"/class/";
    public static final String GET_DISCUSSION = BASE_URL+"/discussion/";
    public static final String GET_POST = BASE_URL+"/post/";
    public static final String GET_ALL_MESSAGES = BASE_URL+"/messages/";
}
