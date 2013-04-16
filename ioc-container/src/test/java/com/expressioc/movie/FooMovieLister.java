package com.expressioc.movie;

public class FooMovieLister {
    private MovieFinder movieFinder;

    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    public MovieFinder getMovieFinder() {
        return movieFinder;
    }
}
