/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.example.quizexample;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.udacity.example.droidtermsprovider.DroidTermsExampleContract;

/**
 * Gets the data from the ContentProvider and shows a series of flash cards.
 */

public class MainActivity extends AppCompatActivity {
    // The current state of the app
    private int mCurrentState;

    private Button mButton;

    private TextView mTextViewWord;

    private TextView mTextViewDefinition;

    // This state is when the word definition is hidden and clicking the button will therefore
    // show the definition
    private final int STATE_HIDDEN = 0;

    // This state is when the word definition is shown and clicking the button will therefore
    // advance the app to the next word
    private final int STATE_SHOWN = 1;

    private Cursor mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the views
        mButton = (Button) findViewById(R.id.button_next);
        mTextViewWord = (TextView) findViewById(R.id.text_view_word);
        mTextViewDefinition = (TextView) findViewById(R.id.text_view_definition);
        new FetchTask().execute();
    }

    /**
     * This is called from the layout when the button is clicked and switches between the
     * two app states.
     *
     * @param view The view that was clicked
     */
    public void onButtonClick(View view) {

        // Either show the definition of the current word, or if the definition is currently
        // showing, move to the next word.
        switch (mCurrentState) {
            case STATE_HIDDEN:
                showDefinition();
                break;
            case STATE_SHOWN:
                nextWord();
                break;
        }
    }

    public void nextWord() {
        // Change button text
        mButton.setText(getString(R.string.show_definition));
        mTextViewDefinition.setVisibility(View.INVISIBLE);
        mCurrentState = STATE_HIDDEN;

        setWordDefinition();
    }

    public void showDefinition() {
        // Change button text
        mButton.setText(getString(R.string.next_word));
        mTextViewDefinition.setVisibility(View.VISIBLE);
        mCurrentState = STATE_SHOWN;
    }

    public class FetchTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(final Void... voids) {
            ContentResolver resolver = getContentResolver();
            //content profvider prefix + content authority + specific data, projection, selection, selection args, sort order
            Cursor cursor = resolver.query(DroidTermsExampleContract.CONTENT_URI, null, null, null, null);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mData = cursor;
            setWordDefinition();
            // initialize the word and definition.
        }
    }

    private void setWordDefinition()
    {
        if(mData == null)
            return;

        int wordCol = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_WORD);
        int definitionCol = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_DEFINITION);
        if(!mData.moveToNext())
            mData.moveToFirst();

            String word = mData.getString(wordCol);
            String definition = mData.getString(definitionCol);

            mTextViewWord.setText(word);
            mTextViewDefinition.setText(definition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.close();
    }
}
