package com.coderafe.opinionated.model;

/**
 * POJO class to represent a single answer
 */
public class Answer {

    private String mUserId;
    private ChoiceInstance mChoiceInstance;

    /**
     * Sets up the reference to the userId and the choiceInstance
     * @param userId The user id
     * @param choiceInstance An instance of a choiceInstance
     */
    public Answer(String userId, ChoiceInstance choiceInstance) {
        mUserId = userId;
        mChoiceInstance = choiceInstance;
    }

    /**
     * Getter to retrieve the user id
     * @return The userId
     */
    public String getUserId() {
        return mUserId;
    }

    /**
     * Getter to retrieve the choiceInstance of this answer
     * @return the choiceInstance of this answer
     */
    public ChoiceInstance getChoiceInstance() {
        return mChoiceInstance;
    }

}
