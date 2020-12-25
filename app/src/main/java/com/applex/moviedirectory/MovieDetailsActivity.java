package com.applex.moviedirectory;

import androidx.appcompat.app.AppCompatActivity;

import com.applex.moviedirectory.model.Movie;
import com.applex.moviedirectory.R;
import com.applex.moviedirectory.utils.Constants;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView title;
    private TextView director;
    private TextView year;
    private TextView runTime;
    private TextView imdbId;
    private TextView genre;
    private TextView writer;
    private TextView actors;
    private TextView plot;
    private TextView rating;
    private TextView dvdRelease;
    private TextView productCompany;
    private TextView country;
    private TextView awards;
    private TextView tvRated;
    private TextView movieType;

    private ImageView poster;

    Movie thisMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        poster = (ImageView) findViewById(R.id.detailsImageId);

        title = (TextView) findViewById(R.id.detailsTitleId);
        year = (TextView) findViewById(R.id.detailsYearId);
        director = (TextView) findViewById(R.id.detailsDirectorId);
        genre = (TextView) findViewById(R.id.detailsGenreId);
        actors = (TextView) findViewById(R.id.detailsActorId);
        writer = (TextView) findViewById(R.id.detailsWriterId);
        plot = (TextView) findViewById(R.id.detailsPlotId);
        awards = (TextView) findViewById(R.id.detailsAwardId);
        productCompany = (TextView) findViewById(R.id.detailsProductCompanyId);
        dvdRelease = (TextView) findViewById(R.id.detailsDvdReleasesYearId);


        Intent intent = getIntent();

        String imdb = intent.getStringExtra("imdbId");

        String Search = Constants.URL_DETAILS + imdb + Constants.API_KEY;

        movieHelper helper = new movieHelper();
        helper.execute(Search);




    }

    public class movieHelper extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {

                URL url = new URL(strings[0]);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(stream);
                int data = reader.read();

                while(data != -1) {
                    char mydata = (char) data;
                    result += mydata;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "F";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s == "F")
                Toast.makeText(getApplicationContext(), "An error occured. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            else {

                try {
                    JSONObject movie = new JSONObject(s);

                    addToMovieList(movie);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void addToMovieList(JSONObject movieObject) throws JSONException {

        Movie movie = new Movie();
        movie.setTitle(movieObject.getString("Title"));
        title.setText(movie.getTitle());

        movie.setYear("Released: " + movieObject.getString("Year"));
        year.setText(movie.getYear());

        movie.setPoster(movieObject.getString("Poster"));
        Picasso.get()
                .load(movie.getPoster())
                .resize(90, 90)
                .placeholder(android.R.drawable.ic_btn_speak_now)
                .into(poster);

        if(movieObject.has("Director")) {
            movie.setDirector("Director : " + movieObject.getString("Director"));
            director.setText(movie.getDirector());
        }

        if(movieObject.has("Genre")) {
            movie.setGenre("Genre : " + movieObject.getString("Genre"));
            genre.setText(movie.getGenre());
        }

        if(movieObject.has("Actors")) {
            movie.setActors("Actors : " + movieObject.getString("Actors"));
            actors.setText(movie.getActors());
        }

        if(movieObject.has("Writer")) {
            movie.setWriter("Writer : " + movieObject.getString("Writer"));
            writer.setText(movie.getWriter());
        }

        if(movieObject.has("Plot")) {
            movie.setPlot("Plot : " + movieObject.getString("Plot"));
            plot.setText(movie.getPlot());
        }

        if(movieObject.has("Awards")) {
            movie.setAwards("Awards : " + movieObject.getString("Awards"));
            awards.setText(movie.getAwards());
        }

        if(movieObject.has("Production")) {
            movie.setProductCompany("Production : " + movieObject.getString("Production"));
            productCompany.setText(movie.getProductCompany());
        }

        if(movieObject.has("DVD")) {
            movie.setDvdRelease("DVD : " + movieObject.getString("DVD"));
            dvdRelease.setText(movie.getDvdRelease());
        }


        Log.d("Movies Details", movie.getTitle());
        Log.d("Images Details", movie.getPoster());
    }


}