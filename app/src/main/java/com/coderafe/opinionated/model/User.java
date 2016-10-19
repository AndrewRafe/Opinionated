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

    /**
     * Constructs all the components of the user model class
     * @param id The userId
     * @param email The email address of the user
     * @param birthYear The birth year of the user
     * @param gender The gender of the user
     * @param postcode The postcode of the user
     * @param questionsAnswered The number of questions the user has already answered
     */
    public User(String id, String email, String birthYear, String gender, String postcode,
                long questionsAnswered) {
        mId = id;
        mEmail = email;
        mBirthYear = birthYear;
        mGender = gender;
        mPostcode = postcode;
        mQuestionsAnswered = questionsAnswered;

    }

    /**
     * Getter method to retrieve the id of the user
     * @return The userId
     */
    public String getId() {
        return mId;
    }

    /**
     * Getter method to retrieve the users email
     * @return The user email
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Getter method to retrieve the birth year of the user
     * @return The user birth year
     */
    public String getBirthYear() {
        return mBirthYear;
    }

    /**
     * Getter method to retrieve the gender of the user
     * @return The users gender
     */
    public String getGender() {
        return mGender;
    }

    /**
     * Getter method to retrieve the postcode of the user
     * @return The users postcode
     */
    public String getPostcode() {
        return mPostcode;
    }

    /**
     * Getter method to retrieve the number of questions the user has answered
     * @return The number of questions the user has answered
     */
    public long getNumQuestionsAnswered() {
        return mQuestionsAnswered;
    }



}
