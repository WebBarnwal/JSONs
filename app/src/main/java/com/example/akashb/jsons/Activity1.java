package com.example.akashb.jsons;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.akashb.jsons.models.MovieModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Activity1 extends AppCompatActivity {

    private TextView tvData;
    private ListView lvMovies;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        dialog= new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading... Please Wait");
        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
           .cacheInMemory(true)
                .cacheOnDisk(true)
           .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
           .defaultDisplayImageOptions(defaultOptions)
           .build();
        ImageLoader.getInstance().init(config); // Do it on Application start


        lvMovies = (ListView) findViewById(R.id.lvMovies);

    }

    public class JSONTask extends AsyncTask<String, String, List<MovieModel>>{

        @Override
        protected void onPreExecute() {
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected List<MovieModel> doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url= new URL(urls[0]);
                connection= (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream stream= connection.getInputStream();
                reader= new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer= new StringBuffer();
                String line;

                while((line= reader.readLine()) != null){
                    buffer.append(line);
                }
                String finalJson= buffer.toString();
                JSONObject parentObj= new JSONObject(finalJson);
                JSONArray parentArr= parentObj.getJSONArray("movies");

                List<MovieModel> movieModelList= new ArrayList<>();
                Gson gson= new Gson();
                for(int i=0; i<parentArr.length(); ++i){
                    JSONObject finalObject= parentArr.getJSONObject(i);
                    MovieModel movieModel= gson.fromJson(finalObject.toString(),MovieModel.class);
                    /*
                    JSONObject finalObject= parentArr.getJSONObject(i);
                    MovieModel movieModel= new MovieModel();
                    movieModel.setMovie(finalObject.getString("movie"));
                    movieModel.setYear(finalObject.getInt("year"));
                    movieModel.setRating((float)finalObject.getDouble("rating"));
                    movieModel.setDirector(finalObject.getString("director"));
                    movieModel.setDuration(finalObject.getString("duration"));
                    movieModel.setTagline(finalObject.getString("tagline"));
                    movieModel.setImage(finalObject.getString("image"));
                    movieModel.setStory(finalObject.getString("story"));

                    List<MovieModel.Cast> castList= new ArrayList<>();
                    for(int j=0; j<finalObject.getJSONArray("cast").length(); ++j){
                        MovieModel.Cast cast= new MovieModel.Cast();
                        cast.setName(finalObject.getJSONArray("cast").getJSONObject(j).getString("name"));
                        castList.add(cast);
                    }
                   movieModel.setCastList(castList);*/

                    // adding final to the object
                    movieModelList.add(movieModel);
                }
                   return movieModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } return null;
        }
        @Override
        protected void onPostExecute(List<MovieModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            MovieAdapter adapter= new MovieAdapter(getApplicationContext(),R.layout.row,result);
            lvMovies.setAdapter(adapter);
         }
   }

   public class MovieAdapter extends ArrayAdapter{
       private List<MovieModel> movieModelList;
       private int resource;
       private LayoutInflater inflater;
       public MovieAdapter(Context context,int resource, List<MovieModel> objects) {
           super(context, resource, objects);
           movieModelList= objects;
           this.resource=resource;
           inflater= (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
       }

       @Override
       public View getView(int position, View convertView, ViewGroup parent) {
           if(convertView==null){
               convertView= inflater.inflate(resource,null);
           }
           ImageView ivMovieIcon;
           TextView tvMovie;
           TextView tvTagline;
           TextView tvDuration;
           TextView tvDirector;
           TextView tvYear;
           RatingBar rbMovieRating;
           TextView tvCast;
           TextView tvStory;

           ivMovieIcon= (ImageView)convertView.findViewById(R.id.tvIcon);
           tvMovie = (TextView)convertView.findViewById(R.id.tvMovie);
           tvTagline = (TextView)convertView.findViewById(R.id.tvTagline);
           tvDuration = (TextView)convertView.findViewById(R.id.tvDuration);
           tvDirector = (TextView)convertView.findViewById(R.id.tvDirector);
           tvYear = (TextView)convertView.findViewById(R.id.tvYear);
           rbMovieRating= (RatingBar)convertView.findViewById(R.id.rbMovie);
           tvCast = (TextView)convertView.findViewById(R.id.tvCast);
           tvStory = (TextView)convertView.findViewById(R.id.tvStory);
           ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), ivMovieIcon);

           tvMovie.setText(movieModelList.get(position).getMovie());
           tvTagline.setText(movieModelList.get(position).getTagline());
           tvDuration.setText("Duration:" + movieModelList.get(position).getDuration());
           tvDirector.setText("Director:"+ movieModelList.get(position).getDirector());
           tvYear.setText("Year:"+ movieModelList.get(position).getYear());
           // ---- ---- RATING BAR ---- ----
           rbMovieRating.setRating(movieModelList.get(position).getRating()/2);

           // ---- ---- CAST LIST ---- ----
           StringBuffer buffer= new StringBuffer();
           for(MovieModel.Cast cast: movieModelList.get(position).getCastList()){
               buffer.append("Cast:"+ cast.getName() + " ,");
           }
           tvCast.setText(buffer);

           tvStory.setText(movieModelList.get(position).getStory());

           return convertView   ;
       }
   }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(id== R.id.action_refresh){
            new JSONTask().execute("https://jsonparsingdemo-cec5b.firebaseapp.com/jsonData/moviesData.txt");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
