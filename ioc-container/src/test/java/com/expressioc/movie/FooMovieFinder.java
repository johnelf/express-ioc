package com.expressioc.movie;

import java.util.Arrays;
import java.util.List;

public class FooMovieFinder implements MovieFinder {

    public List<Movie> findAll() {
        return Arrays.asList(new Movie("movieNameA", "directorA"),
                new Movie("movieNameB", "directorA"),
                new Movie("movieNameC", "directorB"));
    }
}
