package com.coderafe.opinionated.model;

import java.util.LinkedList;

/**
 * Created by Andrew on 21/09/2016.
 * A POJO class to represent a question and all of the questions options
 */
public class Question {

    private String mId;
    private String mQuestion;
    private long mNumChoices;
    private LinkedList<Choice> mChoices;
    private boolean mIsAllChoicesLoaded;
    private String mExtraInformationLink;

    /**
     * Constructor method for the Question class that will set the question and all of the options
     * @param questionId An associated id from a database
     * @param question A question as a String
     * @param numChoices The number of choices that are going to be associated to this question
     * @param extraInformationLink The web link of the extra information relating to this question
     */
    public Question(String questionId, String question,
                    long numChoices, String extraInformationLink) {
        mId = questionId;
        mQuestion = question;
        mNumChoices = numChoices;
        mChoices = new LinkedList<>();
        mExtraInformationLink = extraInformationLink;
        mIsAllChoicesLoaded = false;
    }

    /**
     * Alternate constructor method for creating a question with an already filled allChoices list
     * @param questionId The questionId
     * @param question The text of the question
     * @param numChoices The number of choices the question will have
     * @param allChoices A list of choices for the question
     */
    public Question(String questionId, String question,
                    long numChoices, LinkedList<Choice> allChoices) {
        mId = questionId;
        mQuestion = question;
        mNumChoices = numChoices;
        mChoices = allChoices;
    }

    /**
     * Getter method to return the id number of the question
     * @return questionID
     */
    public String getId() {
        return mId;
    }

    /**
     * Getter method to return the question
     * @return question
     */
    public String getQuestion() {
        return mQuestion;
    }

    /**
     * Getter method to retrieve all of the options
     * @return options An ArrayList containing all of the options for answering the question
     */
    public LinkedList<Choice> getChoices() {
        return mChoices;
    }

    /**
     * Will add a single choice to the list of choices associated with this question
     * @param choice The choice to add to the list of choices
     */
    public void addChoice(Choice choice) {
        mChoices.add(choice);
    }

    /**
     * Will return the number of choices associated with this question
     * @return number of choices in question
     */
    public long getNumChoices() {
        return mNumChoices;
    }

    /**
     * Debug to String method that represents an instance of a question
     * @return A string representation of the question
     */
    public String toString() {
        return mId + " " + mQuestion + " " + mNumChoices;
    }

    /**
     * Getter method to retrieve the web link of the extra information for the question
     * @return The string url of the extra information link
     */
    public String getExtraInformationLink() {
        return mExtraInformationLink;
    }
}
