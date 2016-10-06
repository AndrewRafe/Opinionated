package com.coderafe.opinionated.model;

/**
 * Created by Andrew on 6/10/2016.
 */

public class ChoiceInstance {

    private String mChoiceInstanceId;
    private String mChoiceId;
    private String mQuestionId;

    public ChoiceInstance(String choiceInstanceId, String choiceId, String questionId) {
        mChoiceInstanceId = choiceInstanceId;
        mChoiceId = choiceId;
        mQuestionId = questionId;
    }

    public String getChoiceInstanceId() {
        return mChoiceInstanceId;
    }

    public String getQuestionId() {
        return mQuestionId;
    }

    public String getChoiceId() {
        return mChoiceId;
    }
}
