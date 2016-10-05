package com.coderafe.opinionated.model;

import java.util.ArrayList;

/**
 * Created by Andrew on 21/09/2016.
 * A POJO class to represent a question and all of the questions options
 */
public class Question {

    private int mId;
    private String mQuestion;
    private ArrayList<Choice> mOptions;

    /**
     * Constructor method for the Question class that will set the question and all of the options
     * @param questionId An associated id from a database
     * @param question A question as a String
     * @param options Array list of all possible options to answer the question
     */
    public Question(int questionId, String question, ArrayList<Choice> options) {
        mId = questionId;
        mQuestion = question;
        mOptions = options;
    }

    /**
     * Getter method to return the id number of the question
     * @return questionID
     */
    public int getId() {
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
    public ArrayList<Choice> getOptions() {
        return mOptions;
    }
}
