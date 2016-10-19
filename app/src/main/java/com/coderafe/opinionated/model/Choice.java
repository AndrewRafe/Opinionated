package com.coderafe.opinionated.model;

/**
 * POJO class to represent a single choice
 */
public class Choice {

    private String mChoiceId;
    private String mChoiceText;

    /**
     * Sets up the references to the choiceId and the choiceText
     * @param choiceId The choiceId to store
     * @param choiceText The choiceText to store
     */
    public Choice(String choiceId, String choiceText) {
        mChoiceId = choiceId;
        mChoiceText = choiceText;
    }

    /**
     * Getter method to retrieve the choiceId
     * @return the choiceId of this choice
     */
    public String getChoiceId() {
        return mChoiceId;
    }

    /**
     * Getter to retrieve the choiceText
     * @return the choiceText of this choice
     */
    public String getChoiceText() {
        return mChoiceText;
    }

    /**
     * Debug to string method of the choice
     * @return String representation of the choice
     */
    public String toString() {
        return "ID: " + mChoiceId + ", Choice Text: " + mChoiceText;
    }

}
