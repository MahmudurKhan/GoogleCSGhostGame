/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    private Button btnRestart,btnChallenge;
    private TextView txtGhostText, txtGameStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Log.i("Error","Cant Load Dictionary");
        }




        btnRestart = (Button) findViewById(R.id.RestartBtn);
        btnChallenge = (Button) findViewById(R.id.ChallengeBtn);
        txtGhostText = (TextView) findViewById(R.id.ghostText);
        txtGameStatus = (TextView) findViewById(R.id.gameStatus);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart(v);
            }
        });

        btnChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                challengeHandler();
            }
        });
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        onStart(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORDFRAGMENT", txtGhostText.getText().toString());
        outState.putString("GAMESTATUS", txtGameStatus.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null){
            txtGhostText.setText(savedInstanceState.getString("WORDFRAGMENT"));
            txtGameStatus.setText(savedInstanceState.getString("GAMESTATUS"));
            userTurn = true;
        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    private void challengeHandler() {

        String wordFragment = txtGhostText.getText().toString();
        String possibleWord;
        if (wordFragment.length() >= 4 && dictionary.isWord(wordFragment)){
            txtGameStatus.setText("User Win " + wordFragment + " is a valid word");
        } else {
            possibleWord = dictionary.getAnyWordStartingWith(wordFragment);
            if (possibleWord != null){
                txtGameStatus.setText("Computer Win "+ wordFragment + " is a prefix of word \"" + possibleWord + "\"");
            } else {
                txtGameStatus.setText("User Win " + wordFragment + " is not a prefix of any word");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);

        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);

        String wordFragment = txtGhostText.getText().toString();
        String possibleWord = dictionary.getAnyWordStartingWith(wordFragment);;

        Log.i("word",wordFragment);
        if (wordFragment.length() >= 4 && dictionary.isWord(wordFragment)){
            label.setText("Computer Win "+wordFragment+" is a valid word");
            return;
        } else {
           // possibleWord = dictionary.getAnyWordStartingWith(wordFragment);
           // Log.i("worad",possibleWord);
            if (possibleWord == null){
                label.setText("Computer Win "+ wordFragment + " is not a prefix of any word");
                return;
            } else {
                txtGhostText.setText(txtGhostText.getText().toString()+ possibleWord.charAt(wordFragment.length()));
            }
        }

        userTurn = true;
        label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char c = (char) event.getUnicodeChar();
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')){
            txtGhostText.setText(txtGhostText.getText().toString()+c);
            txtGameStatus.setText(COMPUTER_TURN);
            userTurn = false;
            computerTurn();
        }

        return super.onKeyUp(keyCode, event);

    }
}
