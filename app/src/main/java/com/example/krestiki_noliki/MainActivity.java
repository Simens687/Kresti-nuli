package com.example.krestiki_noliki;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;



public class MainActivity extends AppCompatActivity {
    SharedPreferences themeSettings;
    SharedPreferences.Editor settingsEditor;

    SharedPreferences statistika;
    SharedPreferences.Editor statistikaEditor;

    ImageButton changeTheme;

    private ImageButton[] buttons = new ImageButton[9];

    private boolean isPlayerX = true;
    Button resetButton, statisticButton, botButton, friendButton;
    ImageView who;
    Boolean botX = false;
    TextView tvWinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeSettings = getSharedPreferences("SETTINGS", MODE_PRIVATE);

        if (!themeSettings.contains("MODE_NIGHT_ON")) {
            settingsEditor = themeSettings.edit();
            settingsEditor.putBoolean("MODE_NIGHT_ON", false);
            settingsEditor.apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "С первым запуском)))", Toast.LENGTH_SHORT).show();
        } else {
            setCurrentTheme();
        }


        statistika = getSharedPreferences("STATISTICS", MODE_PRIVATE);

        if (!statistika.contains("X_WINS")) {
            statistikaEditor = statistika.edit();
            statistikaEditor.putInt("X_WINS", 0);
            statistikaEditor.putInt("O_WINS", 0);
            statistikaEditor.putInt("TIES", 0);
            statistikaEditor.apply();
            Toast.makeText(this, "Мы будем собирать статистику", Toast.LENGTH_SHORT).show();
        }



        setContentView(R.layout.activity_main);



        tvWinner = findViewById(R.id.winner);
        resetButton = findViewById(R.id.resetButton);
        who = findViewById(R.id.who);


        changeTheme = findViewById(R.id.themeChangeButton);

        if (!themeSettings.contains("MODE_NIGHT_ON")) {
            changeTheme.setImageResource(R.drawable.ic_moon);
        } else {
            if (!themeSettings.getBoolean("MODE_NIGHT_ON", false)) {
                changeTheme.setImageResource(R.drawable.ic_moon);
            } else {
                changeTheme.setImageResource(R.drawable.ic_sun);
            }
        }

        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                settingsEditor = themeSettings.edit();

                if (!themeSettings.getBoolean("MODE_NIGHT_ON", false)) {
                    settingsEditor.putBoolean("MODE_NIGHT_ON", true);
                } else {
                    settingsEditor.putBoolean("MODE_NIGHT_ON", false);
                }
                settingsEditor.apply();
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        });


        buttons[0] = findViewById(R.id.one);
        buttons[1] = findViewById(R.id.two);
        buttons[2] = findViewById(R.id.three);
        buttons[3] = findViewById(R.id.four);
        buttons[4] = findViewById(R.id.five);
        buttons[5] = findViewById(R.id.six);
        buttons[6] = findViewById(R.id.seven);
        buttons[7] = findViewById(R.id.eight);
        buttons[8] = findViewById(R.id.nine);

        for (int i = 0; i < 9; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onButtonClick(index);
                }
            });
        }

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
                changeTheme.setEnabled(true);
            }
        });

        statisticButton = findViewById(R.id.stat);

        statisticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStatisticDialog();
            }
        });

        botButton = findViewById(R.id.activateBotButton);

        botButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBotDialog();
            }
        });

        friendButton = findViewById(R.id.activateFriendButton);

        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botX = false;
                resetGame();
                for (int i = 0; i < 9; i++) {
                    final int index = i;
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onButtonClick(index);
                        }
                    });
                }
            }
        });
    }

    private void setCurrentTheme() {
        if (!themeSettings.getBoolean("MODE_NIGHT_ON", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    private void onButtonClick(int index) {
        if (isPlayerX) {
            onButtonClickCross(index);
        } else {
            onButtonClickCircle(index);
        }
        isPlayerX = !isPlayerX;
    }
    private void onButtonClickCross(int index){
        buttons[index].setImageResource(R.drawable.cross);
        buttons[index].setContentDescription("X");
        who.setImageResource(R.drawable.circle_32);

        buttons[index].setEnabled(false);
        checkForWin();
    }
    private void onButtonClickCircle(int index){
        buttons[index].setImageResource(R.drawable.circle);
        buttons[index].setContentDescription("O");
        who.setImageResource(R.drawable.cross_32);

        buttons[index].setEnabled(false);
        checkForWin();
    }
    private void checkForWin() {

        String[] board = new String[9];
        for (int i = 0; i < 9; i++) {
            board[i] = buttons[i].getContentDescription().toString();
        }


        int[][] winningCombination = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] combination : winningCombination) {
            String player = board[combination[0]];
            if (!player.isEmpty() && board[combination[1]].equals(player) && board[combination[2]].equals(player)) {
                announceWinner(player);
                changeTheme.setEnabled(true);
                return;
            }
        }

        boolean isTie = true;
        for (int i = 0; i < 9; i++) {
            if (board[i] != "X" && board[i] != "O") {
                isTie = false;
                break;
            }
        }

        if (isTie) {
            announceTie();
        }

    }

    private void announceWinner(String player) {
        if (player.equals("X")) {
            tvWinner.setText("Победили X");
            int value = statistika.getInt("X_WINS", 0);
            statistikaEditor = statistika.edit();
            statistikaEditor.putInt("X_WINS", value + 1);
            statistikaEditor.apply();

        } else if (player.equals("O")) {
            tvWinner.setText("Победили O");
            int value = statistika.getInt("O_WINS", 0);
            statistikaEditor = statistika.edit();
            statistikaEditor.putInt("O_WINS", value + 1);
            statistikaEditor.apply();
        }
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(false);
        };
    }


    private void announceTie() {
        tvWinner.setText("Ничья");
        int value = statistika.getInt("TIES", 0);
        statistikaEditor = statistika.edit();
        statistikaEditor.putInt("TIES", value + 1);
        statistikaEditor.apply();
    }

    private void resetGame() {
        tvWinner.setText("");


        for (int i = 0; i < 9; i++) {
            buttons[i].setImageResource(0);
            buttons[i].setContentDescription("");
        }
        isPlayerX = true;

        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(true);
        };

        who.setImageResource(R.drawable.cross_32);

        if (botX){
            botClick(true);
        }
    }

    private void showStatisticDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_statistics, null);

        TextView crossWinTextView = dialogView.findViewById(R.id.cross_win_tv);
        TextView circleWinTextView = dialogView.findViewById(R.id.circle_win_tv);
        TextView tiesTextView = dialogView.findViewById(R.id.ties_tv);

        crossWinTextView.setText("Победы X: " + statistika.getInt("X_WINS", 0));
        circleWinTextView.setText("Победы O: " + statistika.getInt("O_WINS", 0));
        tiesTextView.setText("Ничьи: " + statistika.getInt("TIES", 0));

        builder.setView(dialogView);

        Dialog statisticDialog = builder.create();
        statisticDialog.show();
    }
    private void showBotDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_hto_ia, null);

        ImageButton cross_chel = dialogView.findViewById(R.id.cross);
        ImageButton circle_chel = dialogView.findViewById(R.id.circle);

        final AlertDialog htoIaDialog = builder.setView(dialogView).create();


        cross_chel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botX = false;
                resetGame();
                for (int i = 0; i < 9; i++) {
                    final int index = i;
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onButtonClickCross(index);
                            botClick(false);
                        }
                    });
                }
                htoIaDialog.dismiss();
            }
        });
        circle_chel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botX = true;
                resetGame();
                for (int i = 0; i < 9; i++) {
                    final int index = i;
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onButtonClickCircle(index);
                            botClick(true);
                        }
                    });
                }
                htoIaDialog.dismiss();
            }
        });
        htoIaDialog.show();
    }

    private void botClick(boolean whoBot){
        while (tvWinner.getText() == "")
        {
            int randomInRange = (int) (Math.random() * (8 - 0 + 1)) + 0;
            if (!buttons[randomInRange].getContentDescription().equals("X") && !buttons[randomInRange].getContentDescription().equals("O")) {
                if (whoBot) {
                    onButtonClickCross(randomInRange);
                } else {
                    onButtonClickCircle(randomInRange);
                }
                break;
            }

        }
    }
}