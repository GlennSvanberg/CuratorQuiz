package com.svanberg.glenn.guesswho;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> curatorURLs = new ArrayList<String>();
    ArrayList<String> curatorNames = new ArrayList<String>();
    int choosenCurator = 0;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];
    Boolean gameIsActive = true;


    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button playAgainButton;


    Integer score = 0;
    Integer count = 0;

    TextView scoreTextView;

    public void playAgain (View view) {



        playAgainButton.setVisibility(view.INVISIBLE);
        Log.i("Visible", "Now");
         count = 0;
         score = 0;
         scoreTextView.setText("");
         gameIsActive = true;




         createNewQuestion();


    }

    public void updateScore() {

        scoreTextView.setText(score + "/" + count);

        if (count == 10) {

                gameIsActive = false;

                playAgainButton.setVisibility(View.VISIBLE);

            if (score == 0) {

                playAgainButton.setText("Du kan ju ingenting!\n Noll rätt svar!!!!\n\n Vill du verkligen spela igen?");

            } else if (score < 3) {

                playAgainButton.setText("Ganska illa du fick bara " + score + " rätt svar!\n\n Spela igen?");


            }   else {

                playAgainButton.setText("Grattis du fick " + score + " rätta svar!\n\n Spela igen?");
            }

        }
    }

    public void curatorChoosen(View view) {

        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {



            Toast.makeText(getApplicationContext(), "Rätt!", Toast.LENGTH_SHORT).show();

            score++;
            count++;
            updateScore();

        } else {

            Toast.makeText(getApplicationContext(), "Fel! Det var " + curatorNames.get(choosenCurator), Toast.LENGTH_SHORT).show();
            count++;
            updateScore();
        }

        createNewQuestion();

    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                    }


                return result;


            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = (TextView) findViewById(R.id.currentScore);
        imageView = (ImageView) findViewById(R.id.imageView);

        button0 = (Button) findViewById(R.id.button1);
        button1 = (Button) findViewById(R.id.button2);
        button2 = (Button) findViewById(R.id.button3);
        button3 = (Button) findViewById(R.id.button4);
        playAgainButton = (Button) findViewById(R.id.playAgainButton);




        DownloadTask task = new DownloadTask();
        String result = null;

        // not 2 of same answer

        //for (String a : answers){
        //  if (celebnames.get(incorrectAnswerLocation)== a){
        //      incorrectAnswerLocation = random.nextInt(celeburls.size());
        //  }
        //  }


        try {

            result = task.execute("http://curator.se/om-curator/kontakta-oss/").get();

            String[] splitResult = result.split("<h2>Kontor</h2>");

            //System.out.println(splitResult[1]);

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[1]);



            while (m.find()) {


                curatorURLs.add(m.group(1));

            }

            p = Pattern.compile("<h4>(.*?)</h4>");
            m = p.matcher(splitResult[1]);


            while (m.find()) {

                try {

                    String[] splitm = m.group(1).split(" ");


                        try {
                            String[] splitmm = splitm[1].split(",");

                            curatorNames.add(splitm[0] + " " + splitmm[0]);

                        } catch (Exception e) {

                            curatorNames.add(splitm[0] + " " + splitm[1]);
                        }


                }   catch (Exception e) {

                    curatorNames.add(m.group(1));

                }

            }


        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();

         }


        createNewQuestion();

        }



    public void createNewQuestion() {


        if (gameIsActive) {

            Random random = new Random();
            choosenCurator = random.nextInt(curatorURLs.size());

            ImageDownloader imageTask = new ImageDownloader();

            Bitmap curatorImage;

            try {
                curatorImage = imageTask.execute(curatorURLs.get(choosenCurator)).get();

                if (curatorURLs.get(choosenCurator).equals("http://curator.se/wp-content/uploads/2012/06/Bild-på-g-140x161-140x161.jpg")) {

                    createNewQuestion();

                } else if (curatorURLs.get(choosenCurator).equals("http://curator.se/wp-content/uploads/2016/07/Vit-bild-140x162.jpg")) {

                    createNewQuestion();

                } else {

                    imageView.setImageBitmap(curatorImage);

                    System.out.println("ImageURL" + curatorURLs.get(choosenCurator));

                    locationOfCorrectAnswer = random.nextInt(4);

                    int incorrectAnswerLocation;

                    for (int i = 0; i < 4; i++) {

                        if (i == locationOfCorrectAnswer) {


                            if (curatorNames.get(choosenCurator) == "") {

                                createNewQuestion();

                            } else {

                                answers[i] = curatorNames.get(choosenCurator);
                            }

                        } else {

                            incorrectAnswerLocation = random.nextInt(curatorURLs.size());


                            while (incorrectAnswerLocation == choosenCurator) {

                                incorrectAnswerLocation = random.nextInt(curatorURLs.size());

                            }

                            if (curatorNames.get(incorrectAnswerLocation).equals("")) {

                                createNewQuestion();

                            } else {

                                answers[i] = curatorNames.get(incorrectAnswerLocation);

                            }

                        }

                    }

                }


                button0.setText(answers[0]);
                button1.setText(answers[1]);
                button2.setText(answers[2]);
                button3.setText(answers[3]);

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

    }
    }

