package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.db.DatabaseReader;
import com.coderafe.opinionated.db.DatabaseWriter;
import com.coderafe.opinionated.model.Choice;
import com.coderafe.opinionated.model.Question;
import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;
import java.util.Random;

/**
 * The Activity that allows the user to answer a selected question.
 * Will create the views depending on the question that the user
 * is going to answer
 */
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

    /**
     * Sets up the answer question activity by establishing read and write connection
     * to the firebase database. Also starts the loading process of the question and
     * the choices for the question
     * @param savedInstanceState
     */
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

    /**
     * Inflates the answer question menu
     * @param menu
     * @return True if the options menu was inflated correctly
     */
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

    /**
     * Async Task to wait for the question that is currently loading in the database reader
     */
    private class DownloadRandomQuestion extends AsyncTask<Void, Void, Question> {

        /**
         * Waits for the question currently being loaded in the database reader
         * @param voids
         * @return The question that the database reader has completed downloading
         */
        @Override
        public Question doInBackground(Void... voids) {
            Question question = mDatabaseReader.getFirstQuestion();
            while (question == null || question.getChoices().size() != question.getNumChoices()) {
                question = mDatabaseReader.getFirstQuestion();
            }
            return question;
        }

        /**
         * Displays the question information to the UI and makes the progress bar invisible
         * @param question The question that was loaded in the database reader
         */
        @Override
        public void onPostExecute(Question question) {
            mQuestion = question;
            mQuestionTextView.setText(question.getQuestion());
            generateChoiceGridLayout();
            mProgressBar.setVisibility(View.GONE);
            Log.d(ASYNC_TASK_TAG, "QUESTION DOWNLOADED");
        }
    }

    /**
     * Creates a read and a write connection to the firebase database
     */
    private void establishReadAndWriteConnection() {
        mDatabaseReader = new DatabaseReader(FirebaseAuth.getInstance().getCurrentUser());
        mDatabaseWriter = new DatabaseWriter(FirebaseAuth.getInstance().getCurrentUser());
    }

    /**
     * Generates the grid layout to correctly display the choices to the questions
     * and sets each of the grids to an appropriate on click listener
     */
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
     * Will store the answer of the question on the database
     */
    private class OnClickSubmitAnswer implements View.OnClickListener {

        private Question mQuestion;
        private Choice mChoice;

        /**
         * Constructor that takes the question and the choice that the user selected
         * @param question The question that the user answered
         * @param choice The choice that the user selected
         */
        public OnClickSubmitAnswer(Question question, Choice choice) {
            this.mQuestion = question;
            this.mChoice = choice;
            Log.d(ON_CLICK_TAG, "" + this.mChoice);
        }

        /**
         * Initiates the async task to find the specific choiceInstanceId that the user
         * has selected
         * @param view
         */
        @Override
        public void onClick(View view) {
            new FindChoiceInstanceId().execute();
        }

        /**
         * Async class that finds the choiceInstanceId and waits for the database reader
         * to return it
         */
        private class FindChoiceInstanceId extends AsyncTask<Void, Void, String> {
            /**
             * Waits for the database reader to find the choiceInstanceId
             * @param voids
             * @return The choiceInstanceId represented as a string
             */
            @Override
            protected String doInBackground(Void... voids) {
                mDatabaseReader.findChoiceInstanceId(mQuestion, mChoice);
                while(mDatabaseReader.getChoiceInstanceId() == null){}
                return mDatabaseReader.getChoiceInstanceId();
            }

            /**
             * Writes the selected choiceInstance to the database and sends the user back home
             * @param s The choiceInstanceId that was found in the doInBackground method
             */
            @Override
            protected void onPostExecute(String s) {
                mDatabaseWriter.submitAnswer(s);
                Intent intent = new Intent(AnswerQuestionActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }

    }


}
