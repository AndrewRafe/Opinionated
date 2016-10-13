package com.coderafe.opinionated.db;

import android.util.Log;

import com.coderafe.opinionated.model.Choice;
import com.coderafe.opinionated.model.Question;
import com.coderafe.opinionated.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.NoSuchElementException;

/**
 * Class to read from the firebase databse given a authorised user to access the information
 * Created by Andrew on 5/10/2016.
 */
public class DatabaseReader {

    private final String LOAD_DATA_ERROR_TAG="LOAD_ERROR";
    private final String CHOICE_LOAD_TAG="CHOICE_LOAD";

    private final String QUESTION_TABLE="questions";
    private final String QUESTION_TEXT_CHILD="questionText";
    private final String QUESTION_ID_CHILD="questionId";
    private final String NUM_CHOICES_CHILD="numChoices";
    private final String CHOICE_ID_CHILD="choiceId";
    private final String CHOICE_INSTANCE_TABLE="choiceInstances";
    private final String CHOICE_TABLE="choices";
    private final String CHOICE_TEXT_CHILD="choiceText";

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

    public DatabaseReader(FirebaseUser user) {
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseUser = user;
        mUserReference = mDatabase.getReference().child(USER_TABLE).child(user.getUid());

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



    public void loadFirstQuestion() {
        final DatabaseReference questionTableReference = mDatabase.getReference().child(QUESTION_TABLE);
        questionTableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String questionId = "0";
                //Get the question text from the database given the questionId
                String questionText = (String) dataSnapshot.child(questionId)
                        .child(QUESTION_TEXT_CHILD).getValue();
                long numChoices = (long) dataSnapshot.child(questionId)
                        .child(NUM_CHOICES_CHILD).getValue();
                mQuestion = new Question(questionId, questionText, numChoices);
                //Search choice instance table to find where questionId is referenced
                DatabaseReference choiceInstanceReference = mDatabase.getReference().child(CHOICE_INSTANCE_TABLE);
                Query query = choiceInstanceReference.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(LOAD_DATA_ERROR_TAG, dataSnapshot.toString());
                        try {
                            for(DataSnapshot singleChoiceInstance: dataSnapshot.getChildren()) {
                                Log.d(LOAD_DATA_ERROR_TAG, singleChoiceInstance.toString());
                                if (singleChoiceInstance.child(QUESTION_ID_CHILD).getValue().toString().equals(mQuestion.getId())) {
                                    String choiceId = singleChoiceInstance.child(CHOICE_ID_CHILD).getValue().toString();
                                    DatabaseReference choiceReference = mDatabase.getReference().child(CHOICE_TABLE).child(choiceId);
                                    choiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Choice choice = new Choice(dataSnapshot.getKey(), (String) dataSnapshot.child(CHOICE_TEXT_CHILD).getValue());
                                            mQuestion.addChoice(choice);
                                            Log.d(CHOICE_LOAD_TAG, "Choice with text: " + choice.getChoiceText());

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

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOAD_DATA_ERROR_TAG, "Failed to load question data");
            }
        });

    }

    public Question getFirstQuestion() {
        return mQuestion;
    }

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
                        if (singleChoiceInstance.child(QUESTION_ID_CHILD).getValue().toString().equals(givenQuestion.getId())
                                && singleChoiceInstance.child(CHOICE_ID_CHILD).getValue().toString().equals(givenChoice.getChoiceId())) {
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

    public String getChoiceInstanceId() {
        return mChoiceInstanceId;
    }

}
