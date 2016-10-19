package com.coderafe.opinionated.db;

import android.util.Log;
import com.coderafe.opinionated.model.Choice;
import com.coderafe.opinionated.model.Question;
import com.coderafe.opinionated.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class to read from the firebase database given an authorised user to access the information
 * Created by Andrew on 5/10/2016.
 */
public class DatabaseReader {

    private final String LOAD_DATA_ERROR_TAG="LOAD_ERROR";
    private final String CHOICE_LOAD_TAG="CHOICE_LOAD";
    private final String LOAD_DATA="LOAD_DATA";
    private final String NULL_TAG="NULL";

    private final String QUESTION_TABLE="questions";
    private final String QUESTION_TEXT_CHILD="questionText";
    private final String QUESTION_ID_CHILD="questionId";
    private final String NUM_CHOICES_CHILD="numChoices";
    private final String QUESTION_LINK_CHILD="infoUrl";
    private final String CHOICE_ID_CHILD="choiceId";
    private final String CHOICE_INSTANCE_TABLE="choiceInstances";
    private final String CHOICE_TABLE="choices";
    private final String CHOICE_TEXT_CHILD="choiceText";
    private final String ANSWER_TABLE="answers";
    private final String CHOICE_INSTANCE_CHILD="choiceInstance";

    private static final String USER_TABLE="users";
    private static final String BIRTH_YEAR_CHILD="birthYear";
    private static final String GENDER_CHILD="gender";
    private static final String POSTCODE_CHILD="postcode";
    private static final String QUESTIONS_ANSWERED_CHILD="questionsAnswered";

    private FirebaseDatabase mDatabase;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mUserReference;
    private User mUser;
    private Question mQuestion;
    private String mChoiceInstanceId;

    //Global Variables for Inner Classes
    //Bar graph global variables
    private BarGraphSeries<DataPoint> mOverallBarGraphPoints;
    private LinkedList<Map<String, Integer>> mResponseCount;
    private int mAnswerCount;
    //List of questions global variables
    private LinkedList<Question> mAllQuestions;
    private int mNumQuestions;
    private LinkedList<String> mAllQuestionIds;
    private boolean mIsAllQuestionIdsLoaded;

    /**
     * Sets up the database reference and initialises member variables
     * @param user
     */
    public DatabaseReader(FirebaseUser user) {
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseUser = user;
        mUserReference = mDatabase.getReference().child(USER_TABLE).child(user.getUid());
        mAnswerCount = 0;
        mAllQuestions = new LinkedList<>();
        mNumQuestions = 0;
        mAllQuestionIds = new LinkedList<>();

    }

    /**
     * Loads the current authority user into the user model stored in this class
     */
    public void loadUser() {
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userId = mFirebaseUser.getUid();
                String email = mFirebaseUser.getEmail();
                String birthYear = (String) dataSnapshot.child(BIRTH_YEAR_CHILD).getValue();
                String gender = (String) dataSnapshot.child(GENDER_CHILD).getValue();
                String postcode = (String) dataSnapshot.child(POSTCODE_CHILD).getValue();
                long questionsAnswered = (long) dataSnapshot
                        .child(QUESTIONS_ANSWERED_CHILD).getValue();
                mUser = new User(userId, email, birthYear, gender, postcode, questionsAnswered);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOAD_DATA_ERROR_TAG, "Failed to load user data");
            }
        });
    }

    /**
     * Returns the current user that has access to read the database
     * @return The User currently accessing the database
     */
    public User getUser() {
        return mUser;
    }


    /**
     * Loads a single question given a questionId
     * @param questionId The questionId that you want to download
     */
    public void loadQuestion(String questionId) {
        mQuestion = null;
        Log.d(LOAD_DATA, "Starting to load question");
        final String givenQuestionId = questionId;
        final DatabaseReference questionTableReference =
                mDatabase.getReference().child(QUESTION_TABLE);
        Log.d(LOAD_DATA, "Question Table Reference Created : " + questionTableReference.toString());

        questionTableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOAD_DATA, "Added listener");
                //Get the question text from the database given the questionId
                try {
                    String questionText = (String) dataSnapshot.child(givenQuestionId)
                            .child(QUESTION_TEXT_CHILD).getValue();
                    long numChoices = (long) dataSnapshot.child(givenQuestionId)
                            .child(NUM_CHOICES_CHILD).getValue();
                    String extraInfoLink = dataSnapshot.child(givenQuestionId)
                            .child(QUESTION_LINK_CHILD).getValue().toString();
                    mQuestion = new Question(givenQuestionId,
                            questionText, numChoices, extraInfoLink);
                } catch (NullPointerException e) {
                    Log.d(LOAD_DATA_ERROR_TAG, "The question id parsed to loadQuestion is null");
                    throw e;
                }

                Log.d(LOAD_DATA, "Size of all questions: " + mAllQuestions.size());
                //Search choice instance table to find where questionId is referenced
                DatabaseReference choiceInstanceReference =
                        mDatabase.getReference().child(CHOICE_INSTANCE_TABLE);
                Query query = choiceInstanceReference.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(LOAD_DATA, dataSnapshot.toString());
                        try {
                            for(DataSnapshot singleChoiceInstance: dataSnapshot.getChildren()) {
                                Log.d(LOAD_DATA, singleChoiceInstance.toString());
                                if (singleChoiceInstance.child(QUESTION_ID_CHILD)
                                        .getValue().toString().equals(mQuestion.getId())) {
                                    String choiceId = singleChoiceInstance.child(CHOICE_ID_CHILD)
                                            .getValue().toString();
                                    DatabaseReference choiceReference = mDatabase.getReference()
                                            .child(CHOICE_TABLE).child(choiceId);
                                    choiceReference.addListenerForSingleValueEvent
                                            (new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Choice choice = new Choice(dataSnapshot.getKey(),
                                                    (String) dataSnapshot.child(CHOICE_TEXT_CHILD)
                                                            .getValue());
                                            mQuestion.addChoice(choice);
                                            Log.d(CHOICE_LOAD_TAG, "Choice with text: "
                                                    + choice.getChoiceText());
                                            try {
                                                if(mQuestion.getNumChoices() ==
                                                        mQuestion.getChoices().size()) {
                                                    mAllQuestions.add(mQuestion);

                                                }
                                            } catch (NullPointerException e) {
                                                Log.d(NULL_TAG,
                                                        "Tried to dereference null question");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                        } catch (NoSuchElementException noSuchElementException) {
                            Log.d(LOAD_DATA_ERROR_TAG, "No choice elements to load");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOAD_DATA_ERROR_TAG, databaseError.toString());
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOAD_DATA_ERROR_TAG, "Failed to load question data");
            }
        });

    }

    /**
     * Retrieves the question stored in this class
     * @return The question currently loaded into this class, could be null if no question loaded
     */
    public Question getFirstQuestion() {
        return mQuestion;
    }

    /**
     * Loads the choice instance id into this class given the question and the choice
     * @param question The question model
     * @param choice The choice model
     */
    public void findChoiceInstanceId(Question question, Choice choice) {

        DatabaseReference dbReference = mDatabase.getReference();
        final Question givenQuestion = question;
        final Choice givenChoice = choice;
        Query query = dbReference.child(CHOICE_INSTANCE_TABLE).orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for(DataSnapshot singleChoiceInstance: dataSnapshot.getChildren()) {
                        if (singleChoiceInstance.child(QUESTION_ID_CHILD)
                                .getValue().toString().equals(givenQuestion.getId())
                                && singleChoiceInstance.child(CHOICE_ID_CHILD)
                                .getValue().toString().equals(givenChoice.getChoiceId())) {
                            mChoiceInstanceId = singleChoiceInstance.getKey().toString();
                        }
                    }

                } catch (NoSuchElementException noSuchElementException) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Loads the bar graph results of the overall responses into this class
     * @param question The question that you want to get the bar graph results of
     */
    public void loadOverallBarGraphResults(Question question) {
        final Question givenQuestion = question;
        mResponseCount = new LinkedList<>();
        LinkedList<Choice> questionChoices = question.getChoices();
        for (int i = 0; i < question.getNumChoices(); i++) {
            HashMap<String, Integer> map = new HashMap<>();
            Log.d("MAP", questionChoices.get(i).getChoiceText());
            map.put(questionChoices.get(i).getChoiceId(), 0);
            mResponseCount.add(map);
        }
        DatabaseReference dbReference = mDatabase.getReference();
        dbReference = dbReference.child(ANSWER_TABLE);
        Query query = dbReference.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleAnswerInstance: dataSnapshot.getChildren()) {
                    DatabaseReference choiceInstanceReference =
                            mDatabase.getReference().child(CHOICE_INSTANCE_TABLE)
                                    .child(singleAnswerInstance.child(CHOICE_INSTANCE_CHILD)
                                            .getValue().toString());
                    choiceInstanceReference.orderByKey().addListenerForSingleValueEvent
                            (new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(QUESTION_ID_CHILD).getValue()
                                    .toString().equalsIgnoreCase(givenQuestion.getId())) {
                                for (Map<String, Integer> map: mResponseCount) {
                                    if (map.containsKey(dataSnapshot.child(CHOICE_ID_CHILD)
                                            .getValue().toString())) {
                                        int count = map.get(dataSnapshot.child(CHOICE_ID_CHILD)
                                                .getValue().toString());
                                        Log.d("DEBUG", "Choice: " + dataSnapshot
                                                .child(CHOICE_ID_CHILD).getValue().toString()
                                                + "Count" + count);
                                        count++;
                                        map.put(dataSnapshot.child(CHOICE_ID_CHILD)
                                                .getValue().toString(), count);
                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    /**
     * Gets the bar graph results that are currently stored in this class
     * @return The bar graph series that is currently loaded in this class,
     *          could be null if nothing has previously been loaded
     */
    public BarGraphSeries<DataPoint> getOverallBarGraphDataPoints() {
        return mOverallBarGraphPoints;
    }

    /**
     * Refreshes the data points given a question
     * @param question The question that you want the data points to display
     */
    public void refreshOverallBarGraphDataPoints(Question question) {
        try {
            DataPoint[] dataPoints = new DataPoint[(int)question.getNumChoices()];

            int i = 0;
            for(Map<String, Integer> map: mResponseCount) {
                dataPoints[i] = new DataPoint(i , map.get(question.getChoices()
                        .get(i).getChoiceId()));
                i++;
            }

            BarGraphSeries<DataPoint> barGraphDataPoints = new BarGraphSeries<>(dataPoints);
            if (mResponseCount.size() < question.getNumChoices()) {
                Log.d("TEST", "Size: " + mResponseCount.size() + ", Num Choices in Question"
                        + question.getNumChoices());
            } else {
                mOverallBarGraphPoints = barGraphDataPoints;
            }
            mOverallBarGraphPoints = barGraphDataPoints;
        } catch (NullPointerException e){
            Log.d("NULL",
                    "null pointer exception in database reader, refreshOverallBarGraphDataPoints");
        }

    }

    /**
     * Gets the currently stored choiceInstanceId
     * @return The choiceInstanceId that is stored in this class, could be null if nothing has been
     *          loaded into the class
     */
    public String getChoiceInstanceId() {
        return mChoiceInstanceId;
    }

    /**
     * Loads all of the questions on the firebase database into the mAllQuestions member variable
     */
    public void loadAllQuestions() {
        mIsAllQuestionIdsLoaded = false;
        DatabaseReference questionTableReference = mDatabase.getReference().child(QUESTION_TABLE);
        Query allQuestionsQuery = questionTableReference.orderByKey();
        allQuestionsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleQuestion:dataSnapshot.getChildren()) {
                    mNumQuestions++;
                    mAllQuestionIds.add(singleQuestion.getKey());
                    Log.d(LOAD_DATA, "Number of questions is: " + mNumQuestions);
                }
                mIsAllQuestionIdsLoaded = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Returns the current list of all questions stored in the mAllQuestions member variable.
     * The list is only completely populated when mAllQuestionsLoaded is true
     * @return
     */
    public LinkedList<Question> getAllQuestions() {
        return mAllQuestions;
    }

    /**
     * Returns the number of questions that have currently been loaded
     * @return
     */
    public int getNumQuestions() {
        return mNumQuestions;
    }

    /**
     * Returns a list of all the currently loaded question ids
     * Check against the isAllQuestionIdsLoaded boolean to see if all
     * of the questions ids have been loaded otherwise it will return a subset
     * of all the questions
     * @return
     */
    public LinkedList<String> getAllQuestionIds() {
        return mAllQuestionIds;
    }

    /**
     * Return whether or not all the question ids have been loaded into the mQuestionIds list
     * @return
     */
    public boolean getIsAllQuestionIdsLoaded() {
        return mIsAllQuestionIdsLoaded;
    }

}
