package com.coderafe.opinionated.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.adapters.QuestionAdapter;
import com.coderafe.opinionated.db.DatabaseReader;
import com.coderafe.opinionated.model.Question;
import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;

/**
 * An activity that displays a list of all the currently active questions in the database
 */
public class QuestionListActivity extends AppCompatActivity {

    private final String LOAD_DATA_TAG="LOAD_DATA_LIST";
    private final String INTENTS_TAG="INTENT";

    private RecyclerView mQuestionListRecyclerView;
    private QuestionAdapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private DatabaseReader mDatabaseReader;

    /**
     * Sets up the references to all the views in the activity as well as setting up the
     * recycler view. It also starts loading all of the questions through an async task
     * @param savedInstanceState Reference to the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        mDatabaseReader = new DatabaseReader(FirebaseAuth.getInstance().getCurrentUser());

        mQuestionListRecyclerView = (RecyclerView) findViewById(R.id.question_list_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mQuestionListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerViewLayoutManager = new LinearLayoutManager(QuestionListActivity.this);
        mQuestionListRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mRecyclerViewAdapter = new QuestionAdapter(this, new LinkedList<Question>(),
                getIntent().getStringExtra(HomeActivity.LIST_PURPOSE));
        Log.d(INTENTS_TAG, "The list purpose is: " + getIntent()
                .getStringExtra(HomeActivity.LIST_PURPOSE));
        mQuestionListRecyclerView.setAdapter(mRecyclerViewAdapter);
        new LoadAllQuestions().execute();
    }

    /**
     * WIll load all the questions in the background and set up the recylcer view when all
     * the questions have been loaded
     */
    private class LoadAllQuestions extends AsyncTask<Void, Question, LinkedList<Question>> {

        /**
         * Gets the database reader to begin to load all of the questions
         */
        @Override
        protected void onPreExecute() {
            mDatabaseReader.loadAllQuestions();
        }

        /**
         * Will continue downloading questions until all the questions have been loaded
         * As each question is loaded it is sent to the progressUpdate method
         * @param voids Params of async task
         * @return A list of all of the questions
         */
        @Override
        protected LinkedList<Question> doInBackground(Void... voids) {
            while(!mDatabaseReader.getIsAllQuestionIdsLoaded()) {
                //wait for all to be downloaded
            }
            LinkedList<Question> questions = new LinkedList<>();
            LinkedList<String> questionIds = mDatabaseReader.getAllQuestionIds();
            //Load all the
            int i = 0;
            while(questions.size() < mDatabaseReader.getAllQuestionIds().size()) {
                mDatabaseReader.loadQuestion(questionIds.get(i));
                while(mDatabaseReader.getFirstQuestion() == null
                        || mDatabaseReader.getFirstQuestion().getChoices().size()
                        < mDatabaseReader.getFirstQuestion().getNumChoices()) {
                    //Do nothing and wait for it to complete
                }
                questions.add(mDatabaseReader.getFirstQuestion());
                publishProgress(mDatabaseReader.getFirstQuestion());
                Log.d(LOAD_DATA_TAG, "Question loaded: "
                        + mDatabaseReader.getFirstQuestion().toString());
                i++;
            }
            Log.d(LOAD_DATA_TAG, "ALL QUESTIONS LOADED AND LEAVING BACKGROUND TASK");
            return questions;
        }

        /**
         * Will update the recycler view adapter to give it the question that was just loaded
         * This is to allow the user to see the questions before everyone has been loaded
         * @param question
         */
        @Override
        protected void onProgressUpdate(Question... question) {
            mRecyclerViewAdapter.addQuestion(question[0]);
            mRecyclerViewAdapter.notifyDataSetChanged();
        }

        /**
         * Will notify debug that all questions have been downloaded
         * @param allQuestions A list of all of the questions in the database
         */
        @Override
        protected void onPostExecute(LinkedList<Question> allQuestions) {
            Log.d(LOAD_DATA_TAG, "All questions loaded");
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


}
