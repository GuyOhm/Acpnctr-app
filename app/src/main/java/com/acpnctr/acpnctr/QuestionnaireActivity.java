package com.acpnctr.acpnctr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.acpnctr.acpnctr.models.Session;

public class QuestionnaireActivity extends AppCompatActivity {

    EditText mYinyang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references to our views
        mYinyang = findViewById(R.id.et_quest_yinyang);

        // initialize list of questions
        initializeQuestionnaire();
    }

    private void initializeQuestionnaire() {
        Intent intent = getIntent();
        if (intent.hasExtra(Session.QUEST_YIN_YANG_KEY)){
            mYinyang.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.questionnaire, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                cancelQuestion();
                return true;
            case R.id.action_save:
                returnSelectedQuestion();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnSelectedQuestion() {
        Intent returnIntent = new Intent();
        Bundle questBundle = new Bundle();
        questBundle.putString("yin_yang", mYinyang.getText().toString().trim());
        returnIntent.putExtras(questBundle);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void cancelQuestion() {
        Intent cancelIntent = new Intent();
        setResult(RESULT_CANCELED, cancelIntent);
        finish();
    }
}
