package com.coderafe.opinionated.activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.db.DatabaseReader;
import com.coderafe.opinionated.model.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.junit.experimental.theories.DataPoints;

public class DataExplorationActivity extends AppCompatActivity {

    private DatabaseReader mDatabaseReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_exploration);

        Log.d("ON_CREATE", "Started Data Exploration Activity");
        establishReadConnection();
        Log.d("ON_CREATE", "Established Connection");
        //TODO: CHOOSE QUESTION ANOTHER WAY

        new DownloadRandomQuestion().execute();
    }

    private void ShowBarGraph(Question question) {
        mDatabaseReader.loadOverallBarGraphResults(question);
        new LoadOverallBarGraphPoints().execute(question);
    }

    private class LoadOverallBarGraphPoints extends AsyncTask<Question, Void, BarGraphSeries<DataPoint>> {

        @Override
        protected BarGraphSeries<DataPoint> doInBackground(Question... params) {
            while(mDatabaseReader.getOverallBarGraphDataPoints(params[0]) == null) {

            }
            return mDatabaseReader.getOverallBarGraphDataPoints(params[0]);
        }

        @Override
        protected void onPostExecute(BarGraphSeries<DataPoint> dataPointBarGraphSeries) {
            super.onPostExecute(dataPointBarGraphSeries);
            GraphView graphView = (GraphView) findViewById(R.id.data_exploration_graph_view);
            graphView.addSeries(dataPointBarGraphSeries);
            // styling
            dataPointBarGraphSeries.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                }
            });

            dataPointBarGraphSeries.setSpacing(50);

// draw values on top
            dataPointBarGraphSeries.setDrawValuesOnTop(true);
            dataPointBarGraphSeries.setValuesOnTopColor(Color.RED);
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
            return question;
        }

        @Override
        public void onPostExecute(Question question) {
            ShowBarGraph(question);
            Log.d("ASYNC_TASK", "QUESTION DOWNLOADED");
        }
    }
}
