package com.expressioc.utility;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClassUtilityTest {

    private String value;

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

    @Test
    public void should_add_zero_for_value_if_basic_type_field_not_set() throws Exception {
        value = "";
        Object param = ClassUtility.assembleParameter(value, int.class);
        assertThat(((Integer)param).intValue(), is(0));
    }

    @Test
    public void should_add_default_value_if_basic_type_field_not_set() throws Exception {
        value = "";
        Object param = ClassUtility.assembleParameter(value, byte.class);
        assertThat(param instanceof Byte, is(true));
    }

    @Test
    public void should_not_use_default_value_when_field_type_is_string_and_not_set() throws Exception {
        value = "";
        Object param = ClassUtility.assembleParameter(value, String.class);
        assertThat(param instanceof String, is(true));
        assertThat(param.equals(""), is(true));
    }

    @Test
    public void should_able_to_assemble_character_from_string() throws Exception {
        value = "h";
        Object param = ClassUtility.assembleParameter(value, Character.class);
        assertThat((Character) param, is(new Character('h')));
    }

    @Test
    public void should_able_to_assemble_char_from_string() throws Exception {
        value = "h";
        Object param = ClassUtility.assembleParameter(value, char.class);
        assertThat((Character) param, is(new Character('h')));
    }
}
