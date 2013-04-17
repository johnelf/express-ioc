package com.expressioc;

import com.expressioc.exception.CycleDependencyException;
import com.expressioc.movie.*;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ConstructorInjectionTest {

    private ExpressContainer container;

    @Before
    public void setUp() {
        container = new ExpressContainer();
        container.setParent(null);
    }

    @Test
    public void should_get_instance_by_default_constructor() {
        MovieLister componentInstance = container.getComponent(MovieLister.class);
        assertNotNull(componentInstance);
    }

    @Test
    public void should_get_instance_by_constructor_with_multiple_parameters() {
        container.addComponent(MovieFinder.class, FooMovieFinder.class);

        MovieLister instance = container.getComponent(MovieLister.class);

        assertThat(instance instanceof MovieLister, is(true));
        assertThat(instance.getMovieFinder(), notNullValue());
    }

    @Test
    public void should_inject_instance_to_constructor() {
        FooMovieFinder movieFinder = new FooMovieFinder();
        container.addComponent(MovieFinder.class, movieFinder);

        MovieLister instance = container.getComponent(MovieLister.class);
        assertThat(instance.getMovieFinder() == movieFinder, is(true));
    }

    @Test(expected = CycleDependencyException.class)
    public void should_throw_exception_when_cycle_dependency_happens() {
        container.addComponent(MovieFinder.class, CycleDependentMovieFinderA.class);
        container.getComponent(MovieFinder.class);
    }

    @Test(expected = CycleDependencyException.class)
    public void should_throw_exception_when_cycle_dependency_happens_in_two_classes() {
        container.addComponent(MovieFinder.class, MovieFinderImplDependOnDirectorFinder.class);
        container.addComponent(DirectorFinder.class, DirectorFinderImplDependOnMovieFinder.class);
        container.getComponent(MovieFinder.class);
    }

    @Test
    public void should_use_setter_injection_to_inject_implementation() {
        container.addComponent(MovieFinder.class, FooMovieFinder.class);
        FooMovieLister instance = container.getComponent(FooMovieLister.class);

        assertThat(instance.getMovieFinder(), is(FooMovieFinder.class));
    }

    @Test
    public void should_get_dependency_from_parent_container() {
        ExpressContainer parentContainer = new ExpressContainer();
        parentContainer.addComponent(MovieFinder.class, FooMovieFinder.class);
        container.setParent(parentContainer);

        MovieLister instance = container.getComponent(MovieLister.class);
        assertThat(instance instanceof MovieLister, is(true));
        assertThat(instance.getMovieFinder(), is(FooMovieFinder.class));
    }

    //TODO: how to fix set again after construction.
    //TODO: make a cache, so a second get can get a previously created object
    //TODO: init container with package name, and init with annotations in specified package.
}
