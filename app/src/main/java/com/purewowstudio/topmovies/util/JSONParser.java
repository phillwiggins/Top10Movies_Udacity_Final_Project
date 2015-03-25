package com.purewowstudio.topmovies.util;

import android.util.Log;

import com.purewowstudio.topmovies.data.FilmDataContract;
import com.purewowstudio.topmovies.models.Film;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Phillip on 18/03/2015.
 */
public class JSONParser {

    static JSONArray jsonarray = null;

    Film[] filmList = new Film[10];
    String FILM_TITLE = FilmDataContract.FilmEntry.COLUMN_FILM_TITLE;
    String FILM_RATING = FilmDataContract.FilmEntry.COLUMN_FILM_RATING;
    String FILM_RUNTIME = FilmDataContract.FilmEntry.COLUMN_FILM_RUNTIME;
    String FILM_CRITICS = FilmDataContract.FilmEntry.COLUMN_FILM_CRITICS;
    String FILM_AUDIENCE = FilmDataContract.FilmEntry.COLUMN_FILM_AUDIENCE;
    String FILM_SYNOPSIS = FilmDataContract.FilmEntry.COLUMN_FILM_SYNOPSIS;
    String FILM_PROFILE = FilmDataContract.FilmEntry.COLUMN_FILM_PROFILE;
    String FILM_RATINGS = "ratings";
    String FILM_POSTERS = "posters";
    String FILM_MOVIES = "movies";

    public JSONParser() {
    }

    public Film[] getJSONFromUrl(String url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("==>", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException ea) {
            ea.printStackTrace();
        }

        try {
            JSONObject jObject = new JSONObject(builder.toString());
            jsonarray = jObject.getJSONArray(FILM_MOVIES);

            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject c = jsonarray.getJSONObject(i);

                String film_title = c.getString(FILM_TITLE);
                String film_rating = c.getString(FILM_RATING);
                String film_runtime = c.getString(FILM_RUNTIME);

                JSONObject ratings = c.getJSONObject(FILM_RATINGS);
                String film_critics = ratings.getString(FILM_CRITICS);
                String film_audience = ratings.getString(FILM_AUDIENCE);

                String film_synopsis = c.getString(FILM_SYNOPSIS);

                JSONObject posters = c.getJSONObject(FILM_POSTERS);
                String film_profile = posters.getString(FILM_PROFILE);
                String posterAmender = "http://content6.flixster.com" + film_profile.substring(92);
                filmList[i] = new Film();
                filmList[i].setTitle(film_title);
                filmList[i].setRating(film_rating);
                filmList[i].setRuntime(film_runtime);
                filmList[i].setCritics(film_critics);
                filmList[i].setAudience(film_audience);
                filmList[i].setSynopsis(film_synopsis);
                filmList[i].setProfile(posterAmender);

            }
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return filmList;
    }
}

