package com.coderafe.opinionated.db;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Class to write information to the firebase database
 */
public class DatabaseWriter {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;

    public DatabaseWriter(FirebaseAuth firebaseAuth) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = firebaseAuth;

    }



}
