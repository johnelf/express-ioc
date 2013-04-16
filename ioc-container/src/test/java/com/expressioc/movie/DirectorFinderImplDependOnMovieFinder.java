package com.expressioc.movie;

public class DirectorFinderImplDependOnMovieFinder implements DirectorFinder{
    private MovieFinder movieFiner;

    public DirectorFinderImplDependOnMovieFinder(MovieFinder movieFinder) {
        this.movieFiner = movieFinder;
    }

    @Override
    public void findDirector() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
