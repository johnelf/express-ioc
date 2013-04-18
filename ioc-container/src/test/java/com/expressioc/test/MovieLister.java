package com.expressioc.test;

import java.util.ArrayList;
import java.util.List;

public class MovieLister {
    private MovieFinder movieFinder;

    public MovieLister() {
    }

    public MovieLister(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    public List<Movie> moviesDirectedBy(String director) {
        List<Movie> movies = new ArrayList<Movie>();

        for (Movie m : this.movieFinder.findAll()) {
            if (director.equals(m.getDirector())) {
                movies.add(m);
            }
        }

        return movies;
    }

    public MovieFinder getMovieFinder() {
        return movieFinder;
    }
}
