package com.coderafe.opinionated.model;

/**
 * Created by Andrew on 6/10/2016.
 */

public class Answer {

    private String mUserId;
    private ChoiceInstance mChoiceInstance;

    public Answer(String userId, ChoiceInstance choiceInstance) {
        mUserId = userId;
        mChoiceInstance = choiceInstance;
    }

    public String getUserId() {
        return mUserId;
    }

    public ChoiceInstance getChoiceInstance() {
        return mChoiceInstance;
    }

}
