package com.expressioc;

import com.expressioc.movie.FooMovieFinder;
import com.expressioc.movie.MovieFinder;
import com.expressioc.movie.MovieLister;
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
}
