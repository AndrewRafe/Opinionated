package com.coderafe.opinionated.model;

/**
 * POJO Class to represent a single choice instance
 */
class ChoiceInstance {

    private String mChoiceInstanceId;
    private String mChoiceId;
    private String mQuestionId;

    /**
     * Sets up all the references required for the choiceInstance
     * @param choiceInstanceId The choiceInstanceId
     * @param choiceId The choiceId
     * @param questionId The questionId
     */
    public ChoiceInstance(String choiceInstanceId, String choiceId, String questionId) {
        mChoiceInstanceId = choiceInstanceId;
        mChoiceId = choiceId;
        mQuestionId = questionId;
    }

    /**
     * Getter method to retrieve the choiceInstanceId
     * @return The choiceInstanceId of this choiceInstance
     */
    public String getChoiceInstanceId() {
        return mChoiceInstanceId;
    }

    /**
     * Getter method to retrieve the questionId
     * @return The questionId of this choiceInstance
     */
    public String getQuestionId() {
        return mQuestionId;
    }

    /**
     * Getter method to retrieve the choiceId
     * @return The choiceId of this choiceInstance
     */
    public String getChoiceId() {
        return mChoiceId;
    }
}
