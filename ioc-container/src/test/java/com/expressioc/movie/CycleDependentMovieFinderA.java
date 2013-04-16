package com.expressioc.movie;

import java.util.List;

public class CycleDependentMovieFinderA implements MovieFinder {
    private MovieFinder movieFinder;

    public CycleDependentMovieFinderA(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    @Override
    public List<Movie> findAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
