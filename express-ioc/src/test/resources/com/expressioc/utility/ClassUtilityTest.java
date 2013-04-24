package com.expressioc.utility;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClassUtilityTest {

    private String value;
    private int a = 1;

    @Test
    public void should_assemble_string_correctly() throws Exception {
        value = "abc";
        assertThat(ClassUtility.assembleParameter(value, String.class) instanceof String, is(true));
    }

    @Test
    public void should_assemble_int_wrapper_correctly() throws Exception {
        value = "1";
        Object param = ClassUtility.assembleParameter(value, Integer.class);
        assertThat(param instanceof Integer, is(true));
        assertThat(((Integer)param).intValue(), is(1));
    }

    @Test
    public void should_assemble_int_correctly() throws Exception {
        value = "1";
        Object param = ClassUtility.assembleParameter(value, int.class);
        assertThat(param instanceof Integer, is(true));
    }

}
