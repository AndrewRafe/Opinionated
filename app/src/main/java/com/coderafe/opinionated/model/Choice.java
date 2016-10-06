package com.coderafe.opinionated.model;

/**
 * POJO class to represent a single choice
 */
public class Choice {

    private String mChoiceId;
    private String mChoiceText;

    public Choice(String choiceId, String choiceText) {
        mChoiceId = choiceId;
        mChoiceText = choiceText;
    }

    public String getChoiceId() {
        return mChoiceId;
    }

    public String getChoiceText() {
        return mChoiceText;
    }

}
