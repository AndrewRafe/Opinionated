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

import java.util.ArrayList;

/**
 * Class to read from the firebase databse given a authorised user to access the information
 */
public class DatabaseReader {

    private final String LOAD_DATA_ERROR_TAG="LOAD_ERROR";

    private final String QUESTION_TABLE="questions";
    private final String QUESTION_TEXT_CHILD="questionText";
    private final String QUESTION_ID_CHILD="questionId";
    private final String CHOICE_INSTANCES_TABLE="choiceInstances";
    private final String CHOICE_ID_CHILD="choiceId";
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

    public Question getFirstQuestion() {
        DatabaseReference questionTableReference = mDatabase.getReference().child(QUESTION_TABLE);
        questionTableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String questionId = "0";
                String questionText = (String) dataSnapshot.child(questionId)
                        .child(QUESTION_TEXT_CHILD).getValue();
                mQuestion = new Question(Integer.parseInt(questionId), questionText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOAD_DATA_ERROR_TAG, "Failed to load question data");
            }
        });

        DatabaseReference choiceInstanceTableReference =
                mDatabase.getReference().child(CHOICE_INSTANCES_TABLE);
        choiceInstanceTableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Query query = dataSnapshot.getRef().orderByChild(QUESTION_ID_CHILD).equalTo(mQuestion.getId());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot singleChild = dataSnapshot.getChildren().iterator().next();
                        while (singleChild != null) {
                            String choiceId = singleChild.child(CHOICE_ID_CHILD).getValue().toString();
                            DatabaseReference choiceReference = mDatabase.getReference()
                                    .child(CHOICE_TABLE).child(choiceId);
                            choiceReference.addListenerForSingleValueEvent
                                    (new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Choice choice = new Choice(dataSnapshot.getKey(), dataSnapshot.child(CHOICE_TEXT_CHILD).getValue().toString());
                                    mQuestion.addChoice(choice);
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return mQuestion;
    }


}
