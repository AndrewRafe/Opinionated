package com.coderafe.opinionated.model;

/**
 * POJO class to represent a single user
 */
public class User {

    private String mId;
    private String mEmail;
    private String mBirthYear;
    private String mGender;
    private String mPostcode;
    private long mQuestionsAnswered;

    public User(String id, String email, String birthYear, String gender, String postcode,
                long questionsAnswered) {
        mId = id;
        mEmail = email;
        mBirthYear = birthYear;
        mGender = gender;
        mPostcode = postcode;
        mQuestionsAnswered = questionsAnswered;

    }

    public String getId() {
        return mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getBirthYear() {
        return mBirthYear;
    }

    public String getGender() {
        return mGender;
    }

    public String getPostcode() {
        return mPostcode;
    }

    public long getNumQuestionsAnswered() {
        return mQuestionsAnswered;
    }



}
