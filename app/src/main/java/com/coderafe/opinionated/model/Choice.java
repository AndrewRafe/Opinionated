package com.coderafe.opinionated.model;

/**
 * POJO class to represent a single choice
 */
public class Choice {

    private long mChoiceId;
    private String mChoiceText;

    public Choice(long choiceId, String choiceText) {
        mChoiceId = choiceId;
        mChoiceText = choiceText;
    }

    public long getChoiceId() {
        return mChoiceId;
    }

    public String getChoiceText() {
        return mChoiceText;
    }

}
