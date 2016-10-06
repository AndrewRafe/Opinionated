package com.coderafe.opinionated.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.db.DatabaseReader;
import com.coderafe.opinionated.model.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class AnswerQuestionActivity extends AppCompatActivity {

    private final String ASYNC_TASK_TAG = "ASYNC";

    private Question mQuestion;
    private DatabaseReader mDatabaseReader;

    private TextView mQuestionTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        mQuestionTextView = (TextView) findViewById(R.id.answer_question_question_tv);
        mProgressBar = (ProgressBar) findViewById(R.id.answer_question_progress_bar);

        establishReadConnection();
        mDatabaseReader.loadFirstQuestion();
        mProgressBar.setVisibility(View.VISIBLE);
        new DownloadRandomQuestion().execute();
    }

    private class DownloadRandomQuestion extends AsyncTask<Void, Void, Question> {

        @Override
        public Question doInBackground(Void... voids) {
            Question question = mDatabaseReader.getFirstQuestion();
            while (question == null) {
                question = mDatabaseReader.getFirstQuestion();
            }
            return question;
        }

        @Override
        public void onPostExecute(Question question) {
            mQuestion = question;
            mQuestionTextView.setText(question.getQuestion());
            mProgressBar.setVisibility(View.GONE);
            Log.d(ASYNC_TASK_TAG, "QUESTION DOWNLOADED");
        }
    }

    private void establishReadConnection() {
        mDatabaseReader = new DatabaseReader(FirebaseAuth.getInstance().getCurrentUser());
    }
}
