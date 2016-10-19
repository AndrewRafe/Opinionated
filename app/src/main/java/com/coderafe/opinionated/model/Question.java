package com.coderafe.opinionated.model;

import java.net.URL;
import java.util.ArrayList;
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
     */
    public Question(String questionId, String question, long numChoices, String extraInformationLink) {
        mId = questionId;
        mQuestion = question;
        mNumChoices = numChoices;
        mChoices = new LinkedList<Choice>();
        mExtraInformationLink = extraInformationLink;
        mIsAllChoicesLoaded = false;
    }

    /**
     * Aleternate constructor method for creating a question with an already filled allCHoices list
     * @param questionId
     * @param question
     * @param numChoices
     * @param allChoices
     */
    public Question(String questionId, String question, long numChoices, LinkedList<Choice> allChoices) {
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
     * @param choice
     */

    public void addChoice(Choice choice) {
        mChoices.add(choice);
    }

    public long getNumChoices() {
        return mNumChoices;
    }

    public String toString() {
        return mId + " " + mQuestion + " " + mNumChoices;
    }

    public String getExtraInformationLink() {
        return mExtraInformationLink;
    }
}
