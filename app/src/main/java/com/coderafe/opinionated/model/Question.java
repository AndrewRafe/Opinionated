package com.coderafe.opinionated.model;

import java.util.ArrayList;

/**
 * Created by Andrew on 21/09/2016.
 * A POJO class to represent a question and all of the questions options
 */
public class Question {

    private int mId;
    private String mQuestion;
    private ArrayList<Choice> mChoices;

    /**
     * Constructor method for the Question class that will set the question and all of the options
     * @param questionId An associated id from a database
     * @param question A question as a String
     */
    public Question(int questionId, String question) {
        mId = questionId;
        mQuestion = question;
        mChoices = new ArrayList<Choice>();
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
    public ArrayList<Choice> getChoices() {
        return mChoices;
    }

    /**
     * Will add a single choice to the list of choices associated with this question
     * @param choice
     */
    public void addChoice(Choice choice) {
        mChoices.add(choice);
    }
}
