package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.db.DatabaseReader;
import com.coderafe.opinionated.db.DatabaseWriter;
import com.coderafe.opinionated.model.Choice;
import com.coderafe.opinionated.model.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class AnswerQuestionActivity extends AppCompatActivity {

    private final String ASYNC_TASK_TAG = "ASYNC";
    private final String UI_GENERATION = "UI_GENERATION";
    private final String ON_CLICK_TAG = "ON_CLICK";
    private final String NULL_TAG = "NULL";
    private final String WEB_TAG = "WEB";

    private Question mQuestion;
    private DatabaseReader mDatabaseReader;
    private DatabaseWriter mDatabaseWriter;

    private TextView mQuestionTextView;
    private ProgressBar mProgressBar;
    private GridLayout mGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        mQuestionTextView = (TextView) findViewById(R.id.answer_question_question_tv);
        mProgressBar = (ProgressBar) findViewById(R.id.answer_question_progress_bar);
        mGridLayout = (GridLayout) findViewById(R.id.answer_question_grid_layout);

        establishReadAndWriteConnection();
        mDatabaseReader.loadQuestion(getIntent().getStringExtra("questionId"));
        mProgressBar.setVisibility(View.VISIBLE);
        new DownloadRandomQuestion().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.answer_question_menu, menu);
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

        if (id == R.id.answer_question_go_home) {
            Intent intent = new Intent(AnswerQuestionActivity.this, HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.answer_question_extra_info) {
            try {
                String infoLink = mQuestion.getExtraInformationLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(infoLink));
                startActivity(intent);
            } catch (NullPointerException e) {
                Log.d(NULL_TAG, "The question does not exist yet");
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private class DownloadRandomQuestion extends AsyncTask<Void, Void, Question> {

        @Override
        public Question doInBackground(Void... voids) {
            Question question = mDatabaseReader.getFirstQuestion();
            while (question == null || question.getChoices().size() != question.getNumChoices()) {
                question = mDatabaseReader.getFirstQuestion();
            }
            return question;
        }

        @Override
        public void onPostExecute(Question question) {
            mQuestion = question;
            mQuestionTextView.setText(question.getQuestion());
            generateChoiceGridLayout();
            mProgressBar.setVisibility(View.GONE);
            Log.d(ASYNC_TASK_TAG, "QUESTION DOWNLOADED");
        }
    }

    private void establishReadAndWriteConnection() {
        mDatabaseReader = new DatabaseReader(FirebaseAuth.getInstance().getCurrentUser());
        mDatabaseWriter = new DatabaseWriter(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void generateChoiceGridLayout() {
        final LinkedList<Choice> choices = mQuestion.getChoices();

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        int column = 2;
        int row;
        if (choices.size() % 2 == 0) {
            row = choices.size()/column;
        } else {
            row = choices.size()/column + 1;
        }
        mGridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        mGridLayout.setColumnCount(column);
        mGridLayout.setRowCount(row);
        TextView choiceText;
        Random random = new Random();
        //Loop through all the grid squares in the grid layout and fill them with the choices
        for (int i = 0, c = 0, r = 0; i < choices.size(); i++, c++) {
            //If the column number is equal to its width then increment row and set column to zero
            if (c == column) {
                c = 0;
                r++;
            }
            choiceText = new TextView(this);
            choiceText.setGravity(Gravity.CENTER);
            choiceText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            choiceText.setBackgroundColor(Color.rgb(random.nextInt()%256, random.nextInt()%256, random.nextInt()%256));
            choiceText.setBackgroundResource(R.drawable.click_ripple);
            Log.d(UI_GENERATION, choices.get(i).getChoiceText());
            choiceText.setText(choices.get(i).getChoiceText());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = screenHeight/(row + 1);
            params.width =  screenWidth/column;
            params.setGravity(Gravity.CENTER);
            params.columnSpec = GridLayout.spec(c);
            params.rowSpec = GridLayout.spec(r);
            choiceText.setLayoutParams(params);

            //Set the on click listener for each choice
            choiceText.setOnClickListener(new OnClickSubmitAnswer(mQuestion, choices.get(i)));

            mGridLayout.addView(choiceText);
        }
    }

    /**
     * On Click Listener class that takes a choice and a question as parameters
     */
    private class OnClickSubmitAnswer implements View.OnClickListener {

        private Question mQuestion;
        private Choice mChoice;

        public OnClickSubmitAnswer(Question question, Choice choice) {
            this.mQuestion = question;
            this.mChoice = choice;
            Log.d(ON_CLICK_TAG, "" + this.mChoice);
        }

        @Override
        public void onClick(View view) {
            new FindChoiceInstanceId().execute();
        }

        private class FindChoiceInstanceId extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                mDatabaseReader.findChoiceInstanceId(mQuestion, mChoice);
                while(mDatabaseReader.getChoiceInstanceId() == null){}
                return mDatabaseReader.getChoiceInstanceId();
            }

            @Override
            protected void onPostExecute(String s) {
                mDatabaseWriter.submitAnswer(s);
                Intent intent = new Intent(AnswerQuestionActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }

    }


}
