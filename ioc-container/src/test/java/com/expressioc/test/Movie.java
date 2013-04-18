package com.expressioc.test;

public class Movie {
    private String name;
    private String director;

    public Movie() {
    }

    public Movie(String name, String director) {
        this.name = name;
        this.director = director;
    }

    public String getDirector() {
        return director;
    }

    @Override
    public String toString() {
        return "Movie{ name='" + name +  "', director='" + director + "'}";
    }
}
