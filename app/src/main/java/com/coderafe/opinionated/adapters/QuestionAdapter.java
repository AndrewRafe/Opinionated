package com.coderafe.opinionated.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coderafe.opinionated.R;
import com.coderafe.opinionated.activities.AnswerQuestionActivity;
import com.coderafe.opinionated.activities.DataExplorationActivity;
import com.coderafe.opinionated.activities.HomeActivity;
import com.coderafe.opinionated.model.Question;

import java.util.LinkedList;

/**
 * Class that handles the creation of the adapter for a list of questions for use in displaying
 * all the questions in a recycler view list
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private LinkedList<Question> mAllQuestions;
    private Context mContext;
    private String mListPurpose;

    /**
     * Question adapter constructor takes the list of question that the recycler view
     * is to show through the adapter
     * @param allQuestions An array of all the questions to be shown
     */
    public QuestionAdapter(Context context, LinkedList<Question> allQuestions, String listPurpose) {
        mListPurpose = listPurpose;
        mAllQuestions = allQuestions;
        mContext = context;
    }


    /**
     * View Holder class defines a single question item view holder in the wider list
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView questionListItemTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            questionListItemTextView = (TextView) itemView.findViewById(R.id.question_list_item_tv);
        }
    }

    /**
     * Inflates the view holder from the question list item layout xml file
     * @param parent
     * @param viewType
     * @return The inflated view holder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.question_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * Sets the text of the current view holder to be equal to the current questions text
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Question currentQuestion = mAllQuestions.get(position);
        holder.questionListItemTextView.setText(currentQuestion.getQuestion());
        holder.questionListItemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListPurpose.equalsIgnoreCase(HomeActivity.ANSWER_QUESTION_PURPOSE)) {
                    Intent intent = new Intent(mContext, AnswerQuestionActivity.class);
                    intent.putExtra("questionId", currentQuestion.getId());
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, DataExplorationActivity.class);
                    intent.putExtra("questionId", currentQuestion.getId());
                    mContext.startActivity(intent);
                }

            }
        });
    }

    /**
     * Gets the number of items in the overall question list
     * @return Number of questions in list
     */
    @Override
    public int getItemCount() {
        return mAllQuestions.size();
    }

    /**
     * Adds a single question to the list of questions
     * @param question A single question
     */
    public void addQuestion(Question question) {
        mAllQuestions.add(question);
    }

    /**
     * Adds a list of questions onto the end of the list of questions stored
     * in this class
     * @param questions List of questions
     */
    public void addListOfQuestions(LinkedList<Question> questions) {
        for(Question question: questions) {
            mAllQuestions.add(question);
        }
    }
}