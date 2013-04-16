package com.expressioc.movie;

import java.util.List;

public class MovieFinderImplDependOnDirectorFinder implements MovieFinder{
    private DirectorFinder directorFinder;

    public MovieFinderImplDependOnDirectorFinder(DirectorFinder directorFinder) {
        this.directorFinder = directorFinder;
    }

    @Override
    public List<Movie> findAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
