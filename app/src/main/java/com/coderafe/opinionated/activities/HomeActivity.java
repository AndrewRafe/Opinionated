package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.adapters.QuestionAdapter;
import com.coderafe.opinionated.db.DatabaseReader;
import com.coderafe.opinionated.model.Question;
import com.coderafe.opinionated.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;

/**
 * The home activity is the screen that the user will be on when they login or re-enter the app
 * after already logging in. From here they can navigate to answer questions and view question
 * results
 */
public class HomeActivity extends AppCompatActivity {

    public static final String LIST_PURPOSE = "listPurpose";
    public static final String ANSWER_QUESTION_PURPOSE = "answer";
    public static final String EXPLORE_DATA_PURPOSE = "explore";

    private DatabaseReader mDatabaseReader;
    private User mUser;

    private TextView mWelcomeTextView;
    private TextView mQuestionsAnsweredTextView;
    private Button mAnswerMoreQuestionsButton;
    private TextView mExploreResultsTextView;
    private Button mExploreResultsButton;

    private RelativeLayout mMainLayout;
    private ProgressBar mLoadDataProgressBar;

    /**
     * Sets up all member variables and views when the activity is created
     * Creates an instance of the database reader and reads in the currently
     * authorised user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        establishReadConnection();
        //mUser = mDatabaseReader.getUser();

        mWelcomeTextView = (TextView) findViewById(R.id.home_welcome_tv);
        mQuestionsAnsweredTextView = (TextView) findViewById(R.id.home_questions_answered_tv);
        mAnswerMoreQuestionsButton = (Button) findViewById(R.id.home_answer_questions_btn);
        mExploreResultsTextView = (TextView) findViewById(R.id.home_explore_results_tv);
        mExploreResultsButton = (Button) findViewById(R.id.home_explore_results_btn);
        mMainLayout = (RelativeLayout) findViewById(R.id.home_main_layout);
        mLoadDataProgressBar = (ProgressBar) findViewById(R.id.home_load_data_progress_bar);

        mExploreResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, QuestionListActivity.class);
                intent.putExtra(LIST_PURPOSE, EXPLORE_DATA_PURPOSE);
                startActivity(intent);
            }
        });

        new LoadUserData().execute();

        mAnswerMoreQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, QuestionListActivity.class);
                intent.putExtra(LIST_PURPOSE, ANSWER_QUESTION_PURPOSE);
                startActivity(intent);
            }
        });


    }

    /**
     * Refreshes the UI elements in the activity when the activity is resumed
     */
    @Override
    protected void onStart() {
        super.onStart();
        refreshPage();
    }

    /**
     * Inflates the menu on the action bar from a layout
     * @param menu
     * @return Whether the options menu was created successfully
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    /**
     * Handles any of the options that are selected on the action menu
     * @param menuItem
     * @return Whether the menu option was handled correctly
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.home_menu_refresh) {
            refreshPage();
        } else if (id == R.id.home_menu_logout) {
            logout();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Creates an instance of the database reader class that allows the communication
     * with the firebase database
     */
    private void establishReadConnection() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReader = new DatabaseReader(firebaseAuth.getCurrentUser());
    }

    /**
     * Async Task that loads the user data off the main thread to be displayed in this activity
     */
    private class LoadUserData extends AsyncTask<Void, Void, User> {

        /**
         * Before the async task begins the views on this activity will be hidden
         */
        @Override
        public void onPreExecute() {
            makeViewVisible(false);
        }

        /**
         * While the database reader is attempting to read the user data, this waits until it has
         * been loaded before updating any of the UI elements related to the user
         * @param voids
         * @return
         */
        @Override
        public User doInBackground(Void... voids) {
            User user = mDatabaseReader.getUser();
            while (user == null) {
                user = mDatabaseReader.getUser();
            }
            return user;
        }

        /**
         * Sets the user retrieved from the doInBackground method and stores in the mUser
         * member variable. It also sets up the text for the activity and removes the progress
         * bar and makes all the other views visible
         * @param user The user loaded in the doInBackground method
         */
        @Override
        public void onPostExecute(User user) {
            mUser = user;
            Log.d("ASYNC", "USER DATA LOADED");
            mWelcomeTextView.setText(getString(R.string.home_welcome_message)
                    + " " + mUser.getEmail());

            mQuestionsAnsweredTextView.setText(getString(R.string.home_questions_answered_message_part1)
                    + " " + mUser.getNumQuestionsAnswered() + " "
                    + getString(R.string.home_questions_answered_message_part2));
            makeViewVisible(true);
        }

    }

    /**
     * Helper method to make the activity show the loading bar if the views should not be visible
     * @param isVisible Whether the activity views should be visible or not
     */
    private void makeViewVisible(boolean isVisible) {
        if (isVisible) {
            mQuestionsAnsweredTextView.setVisibility(View.VISIBLE);
            mWelcomeTextView.setVisibility(View.VISIBLE);
            mAnswerMoreQuestionsButton.setVisibility(View.VISIBLE);
            mExploreResultsButton.setVisibility(View.VISIBLE);
            mExploreResultsTextView.setVisibility(View.VISIBLE);
            mLoadDataProgressBar.setVisibility(View.GONE);
        } else {
            mQuestionsAnsweredTextView.setVisibility(View.GONE);
            mWelcomeTextView.setVisibility(View.GONE);
            mAnswerMoreQuestionsButton.setVisibility(View.GONE);
            mExploreResultsButton.setVisibility(View.GONE);
            mExploreResultsTextView.setVisibility(View.GONE);
            mLoadDataProgressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * On click method that refreshes the information on the page
     */
    public void refreshPage() {
        mDatabaseReader.clearAllReadData();
        mDatabaseReader.loadUser();
        new LoadUserData().execute();
    }

    /**
     * On click method for the logout option in the action bar menu. Will send the user back
     * to the title activity as well as suspending their authentication to the database
     */
    public void logout() {
        Intent intent = new Intent(HomeActivity.this, TitleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
