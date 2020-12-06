package com.mymovielist.moviedirectory.Activity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mymovielist.moviedirectory.Data.MovieRecyclerViewAdapter;
import com.mymovielist.moviedirectory.Model.Movie;
import com.mymovielist.moviedirectory.R;
import com.mymovielist.moviedirectory.Utils.Constants;
import com.mymovielist.moviedirectory.Utils.Prefs;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.android.volley.Request.*;
import static com.mymovielist.moviedirectory.Utils.Constants.API_KEY;
import static com.mymovielist.moviedirectory.Utils.Constants.URL_LEFT;
import static com.mymovielist.moviedirectory.Utils.Constants.URL_RIGHT;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    private List<Movie> movieList;

    private Context context;

    private RequestQueue queue;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private Prefs myprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //queue = Volley.newRequestQueue(this);

        context = this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        movieList = new ArrayList<>();

        myprefs = new Prefs(MainActivity.this);

        String search = myprefs.getSearch();

        String url = URL_LEFT + search + API_KEY;

        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(this, movieList);
        recyclerView.setAdapter(movieRecyclerViewAdapter);

        movieHelper helper = new movieHelper();
        helper.execute(url);

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

            }
            catch (Exception e) {
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
                    JSONObject downloaded = new JSONObject(s);
                    JSONArray movies = downloaded.getJSONArray("Search");

                    addToMovieList(movies);

                } catch (JSONException e) {
                    e.printStackTrace();


                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void addToMovieList(JSONArray movies) throws JSONException {
        try {
            movieList.clear();
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movieObject = movies.getJSONObject(i);

                Movie movie = new Movie();
                movie.setTitle(movieObject.getString("Title"));
                movie.setYear("Released: " + movieObject.getString("Year"));
                movie.setImdbId(movieObject.getString("imdbID"));
                movie.setMovieType("Type : " + movieObject.getString("Type"));
                movie.setPoster(movieObject.getString("Poster"));

                movieList.add(movie);

                Log.d("Movies", movie.getTitle());
                Log.d("Images", movie.getPoster());
            }

            movieRecyclerViewAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get movies

    public List<Movie> getMovies(String searchItem) {
        movieList.clear();

        JSONObject myJsonObject = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL_LEFT + searchItem + API_KEY, myJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray moviesArray = response.getJSONArray("Search");

                    for(int i = 0; i < moviesArray.length(); i++) {
                        JSONObject movieObject = moviesArray.getJSONObject(i);

                        Movie movie = new Movie();
                        movie.setTitle(movieObject.getString("Title"));
                        movie.setYear(movieObject.getString("Year"));
                        movie.setImdbId(movieObject.getString("imdbID"));
                        movie.setMovieType(movieObject.getString("Type"));
                        movie.setPoster(movieObject.getString("Poster"));

                        movieList.add(movie);

                        Log.d("Movies", movie.getTitle());
                        Log.d("Images", movie.getPoster());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);
        return movieList;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_search) {

            showInputDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showInputDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        final EditText editText = (EditText) view.findViewById(R.id.searchEdittext);
        final Button submitbutton = (Button) view.findViewById(R.id.submit_button);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs prefs = new Prefs(MainActivity.this);

                if(!editText.getText().toString().isEmpty()) {
                    String search = editText.getText().toString();

                    movieList.clear();

                    String url = URL_LEFT + search + API_KEY;

                    movieHelper helper = new movieHelper();
                    helper.execute(url);

                    prefs.setSearch(search);

                    Log.i("checkk", "size is " + movieList.size());

                }
                dialog.dismiss();
            }
        });
    }
}