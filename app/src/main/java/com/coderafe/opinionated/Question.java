package com.coderafe.opinionated;

import java.util.ArrayList;

/**
 * Created by Andrew on 21/09/2016.
 * A POJO class to represent a question and all of the questions options
 */
public class Question {

    private String mQuestion;
    private ArrayList<String> mOptions;

    /**
     * Constructor method for the Question class that will set the question and all of the options
     * @param question A question as a String
     * @param options Array list of all possible options to answer the question
     */
    public Question(String question, ArrayList<String> options) {
        mQuestion = question;
        mOptions = options;
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
    public ArrayList<String> getOptions() {
        return mOptions;
    }
}
