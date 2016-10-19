package com.coderafe.opinionated.activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.db.DatabaseReader;
import com.coderafe.opinionated.model.Choice;
import com.coderafe.opinionated.model.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.junit.experimental.theories.DataPoints;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class DataExplorationActivity extends AppCompatActivity {

    private final String NULL_ERROR_TAG = "NULL";
    private final String BAR_GRAPH = "BAR_GRAPH";
    private final String QUESTION_TAG = "QUESTION";
    private final int REFRESH_TIME = 5000;

    private DatabaseReader mDatabaseReader;
    private BarGraphSeries<DataPoint> mBarGraphDataPoints;
    private GraphView mGraph;
    private Question mQuestion;

    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_exploration);
        mGraph = (GraphView) findViewById(R.id.data_exploration_graph_view);
        mGraph.setVisibility(View.GONE);
        establishReadConnection();
        String questionId = getIntent().getStringExtra("questionId");
        Log.d(QUESTION_TAG, "Question id " + questionId);
        mDatabaseReader.loadQuestion(questionId);
        new DownloadRandomQuestion().execute();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mDatabaseReader.refreshOverallBarGraphDataPoints(mQuestion);
                Log.d("TIMER", "Timer Tick");
                drawGraph();
            }
        }, 0, REFRESH_TIME);
        //TODO: CHOOSE QUESTION ANOTHER WAY

    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
    }

    private void showBarGraph(Question question) {
        mGraph.setVisibility(View.VISIBLE);
        graphSetUp(question);
        mDatabaseReader.loadOverallBarGraphResults(question);
        Log.d("BAR_GRAPH", "loading bar graph results");
        new LoadOverallBarGraphPoints().execute(question);
    }

    private class LoadOverallBarGraphPoints extends AsyncTask<Question, Void, BarGraphSeries<DataPoint>> {

        @Override
        protected BarGraphSeries<DataPoint> doInBackground(Question... params) {
            while(true) {
                mBarGraphDataPoints = mDatabaseReader.getOverallBarGraphDataPoints();
            }
        }

        @Override
        protected void onPostExecute(BarGraphSeries<DataPoint> dataPointBarGraphSeries) {
            super.onPostExecute(dataPointBarGraphSeries);
            Log.d("BREAK", "Broke out of async");
            mBarGraphDataPoints = dataPointBarGraphSeries;
        }
    }

    private void graphSetUp(Question question) {
        String[] choiceText = new String[(int)question.getNumChoices()];
        StaticLabelsFormatter labelsFormatter = new StaticLabelsFormatter(mGraph);

        int i = 0;
        for(Choice choice: question.getChoices()) {
            choiceText[i] = choice.getChoiceText();
            i++;
        }
        labelsFormatter.setHorizontalLabels(choiceText);

    }

    private void drawGraph() {
        try {
            mGraph.removeAllSeries();
            mGraph.addSeries(mBarGraphDataPoints);
            Log.d("DRAW", "Drawing points");
            // styling
            mBarGraphDataPoints.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                }
            });

            mBarGraphDataPoints.setSpacing(1);

            // draw values on top
            mBarGraphDataPoints.setDrawValuesOnTop(true);
            mBarGraphDataPoints.setValuesOnTopColor(Color.RED);
            Viewport viewport = mGraph.getViewport();
            viewport.setMinY(0);
            viewport.setScalableY(true);
            viewport.setYAxisBoundsManual(true);

        } catch (NullPointerException e) {
            Log.d(NULL_ERROR_TAG, "Data points are null");
        }


    }

    private void establishReadConnection() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReader = new DatabaseReader(firebaseAuth.getCurrentUser());
    }

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
