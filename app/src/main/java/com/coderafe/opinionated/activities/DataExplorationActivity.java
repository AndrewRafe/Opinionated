package com.coderafe.opinionated.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.db.DatabaseReader;
import com.coderafe.opinionated.model.Choice;
import com.coderafe.opinionated.model.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity that graphs all users answers to the questions
 */
public class DataExplorationActivity extends AppCompatActivity {

    private final String NULL_ERROR_TAG = "NULL";
    private final String BAR_GRAPH = "BAR_GRAPH";
    private final String QUESTION_TAG = "QUESTION";
    private final int REFRESH_TIME = 1000;

    private DatabaseReader mDatabaseReader;
    private BarGraphSeries<DataPoint> mBarGraphDataPoints;
    private GraphView mGraph;
    private Question mQuestion;
    private TextView mTitleTextView;

    private static Timer sTimer;

    /**
     * Sets up references to all the views in the data exploration activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_exploration);
        mGraph = (GraphView) findViewById(R.id.data_exploration_graph_view);
        mTitleTextView = (TextView) findViewById(R.id.data_exploration_title_tv);
    }

    /**
     * OnStart method that establishes a read connection to the database as well as
     * setting up a timer tick that will refresh the graph at a specified time interval
     */
    @Override
    protected void onStart() {
        super.onStart();
        establishReadConnection();
        String questionId = getIntent().getStringExtra("questionId");
        Log.d(QUESTION_TAG, "Question id " + questionId);
        mDatabaseReader.loadQuestion(questionId);
        new DownloadRandomQuestion().execute();
        sTimer = new Timer();
        sTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mDatabaseReader.refreshOverallBarGraphDataPoints(mQuestion);
                Log.d("TIMER", "Timer Tick");
                drawGraph();
            }
        }, 0, REFRESH_TIME);
    }

    /**
     * OnStop method that stops the timer when the activity stops
     */
    @Override
    protected void onStop() {
        super.onStop();
        sTimer.cancel();
    }

    /**
     * Inflates the menu for the data exploration activity
     * @param menu A reference to the meny
     * @return True if the menu was inflated correctly
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    /**
     * Handles any of the options that are selected on the action menu
     * @param menuItem A reference to a menu item
     * @return Whether the menu option was handled correctly
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.home_menu_refresh) {
            establishReadConnection();
            String questionId = getIntent().getStringExtra("questionId");
            Log.d(QUESTION_TAG, "Question id " + questionId);
            mDatabaseReader.loadQuestion(questionId);
            new DownloadRandomQuestion().execute();
        } else if (id == R.id.home_menu_logout) {
            establishReadConnection();
        } else if (id == R.id.home_menu_go_home) {
            Intent intent = new Intent(DataExplorationActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * The initial display of the bar graph at the conclusion of the loading of the question
     * from the database reader
     * @param question The question that was loaded from the database reader
     */
    private void showBarGraph(Question question) {
        mGraph.setVisibility(View.VISIBLE);
        graphSetUp(question);
        mDatabaseReader.loadOverallBarGraphResults(question);
        Log.d("BAR_GRAPH", "loading bar graph results");
        //Loads the results of the question
        new LoadOverallBarGraphPoints().execute(question);
    }

    /**
     * Async class that loads all of the data-points into the member variable storing the
     * database points
     */
    private class LoadOverallBarGraphPoints extends
            AsyncTask<Question, Void, BarGraphSeries<DataPoint>> {

        /**
         * Constantly retrieves the loaded data points for display in the graph view
         * @param params
         * @return
         */
        @Override
        protected BarGraphSeries<DataPoint> doInBackground(Question... params) {
            while(true) {
                mBarGraphDataPoints = mDatabaseReader.getOverallBarGraphDataPoints();
            }
        }

        /**
         * If it breaks out of the async task
         * @param dataPointBarGraphSeries
         */
        @Override
        protected void onPostExecute(BarGraphSeries<DataPoint> dataPointBarGraphSeries) {
            super.onPostExecute(dataPointBarGraphSeries);
            Log.d("BREAK", "Broke out of async");
            mBarGraphDataPoints = dataPointBarGraphSeries;
        }
    }

    /**
     * Sets up the conditions of the graph view
     * @param question The question that the graph view is going to display
     */
    private void graphSetUp(Question question) {
        String[] choiceText = new String[(int)question.getNumChoices()];
        StaticLabelsFormatter labelsFormatter = new StaticLabelsFormatter(mGraph);
        mTitleTextView.setText(question.getQuestion());
        int i = 0;
        Log.d(QUESTION_TAG, "Number of Choices for question " + question.getChoices().size());
        for(Choice choice: question.getChoices()) {
            choiceText[i] = choice.getChoiceText();
            Log.d(QUESTION_TAG, "adding choice " + choice.toString());
            i++;
        }
        labelsFormatter.setHorizontalLabels(choiceText);
        mGraph.getGridLabelRenderer().setLabelFormatter(labelsFormatter);


    }

    /**
     * Will draw the graph according to the current graph data points
     */
    private void drawGraph() {
        try {
            mGraph.removeAllSeries();
            mGraph.addSeries(mBarGraphDataPoints);
            Log.d("DRAW", "Drawing points");
            // styling
            mBarGraphDataPoints.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX()*255/4,
                            (int) Math.abs(data.getY()*255/6), 100);
                }
            });

            mBarGraphDataPoints.setSpacing(1);

            // draw values on top
            mBarGraphDataPoints.setDrawValuesOnTop(true);
            mBarGraphDataPoints.setValuesOnTopColor(Color.RED);

            Viewport viewport = mGraph.getViewport();
            //Set the max height of the x axis to be a fifth lower than the highest bar
            viewport.setMinY(0);
            viewport.setMaxY(mBarGraphDataPoints.getHighestValueY()
                    + mBarGraphDataPoints.getHighestValueY()/5);
            viewport.setYAxisBoundsManual(true);

        } catch (NullPointerException e) {
            Log.d(NULL_ERROR_TAG, "Data points are null");
        }


    }

    /**
     * Will establish a read connection to the firebase database and store the refrence
     * to the database reader
     */
    private void establishReadConnection() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReader = new DatabaseReader(firebaseAuth.getCurrentUser());
    }

    /**
     * Will download the question of the currently loaded question in the database reader
     */
    private class DownloadRandomQuestion extends AsyncTask<Void, Void, Question> {

        @Override
        public Question doInBackground(Void... voids) {
            Question question = mDatabaseReader.getFirstQuestion();
            while (question == null || question.getChoices().size() != question.getNumChoices()) {
                question = mDatabaseReader.getFirstQuestion();
            }
            mQuestion = question;
            if (question.getNumChoices() == question.getChoices().size()) {
                Log.d(QUESTION_TAG, "Question loaded has correct number of choices");
            }
            Log.d(QUESTION_TAG, "Question Loaded " + question.toString());
            return question;
        }

        @Override
        public void onPostExecute(Question question) {
            showBarGraph(question);
        }
    }


}
