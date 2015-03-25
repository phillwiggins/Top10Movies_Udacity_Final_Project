package com.purewowstudio.topmovies.models;

/**
 * Created by Phillip on 19/03/2015.
 */
public class Film {

    private int id;
    private String title;
    private String rating;
    private String runtime;
    private String critics;
    private String audience;
    private String synopsis;
    private String profile;

    public Film() {
    }

    public Film(String title, String rating, String runtime, String critics, String audience, String synopsis, String profile) {
        super();
        this.title = title;
        this.rating = rating;
        this.runtime = runtime;
        this.critics = critics;
        this.audience = audience;
        this.synopsis = synopsis;
        this.profile = profile;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public void setCritics(String critics) {
        this.critics = critics;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getCritics() {
        return critics;
    }

    public String getAudience() {
        return audience;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getProfile() {
        return profile;
    }

    @Override
    public String toString() {
        return "Film [id=" + id + ", title=" + title + ", rating=" + rating
                + ", runtime=" + runtime + ", critics=" + critics + ", audience=" + audience
                + ", synopsis=" + synopsis + ", profile=" + profile
                + "]";
    }
}

