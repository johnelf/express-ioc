package com.expressioc;

import com.expressioc.exception.CycleDependencyException;
import com.expressioc.test.*;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class InjectionTest {

    private ExpressContainer container;

    @Before
    public void setUp() {
        container = new ExpressContainer();
        container.setParent(null);
    }

    @Test
    public void should_get_instance_by_default_constructor() {
        WithDefaultConstructor instance = container.getComponent(WithDefaultConstructor.class);
        assertNotNull(instance);
    }

    @Test
    public void should_get_instance_by_constructor_with_multiple_parameters() {
        container.addComponent(IA.class, IAImpl.class);

        IA_As_ConstructorArg instance = container.getComponent(IA_As_ConstructorArg.class);

        assertThat(instance instanceof IA_As_ConstructorArg, is(true));
        assertThat(instance.getInterfaceA(), notNullValue());
    }

    @Test
    public void should_inject_instance_to_constructor() {
        IA interfaceAImplInstance = new IAImpl();
        container.addComponent(IA.class, interfaceAImplInstance);

        IA_As_ConstructorArg instance = container.getComponent(IA_As_ConstructorArg.class);
        assertThat(instance.getInterfaceA() == interfaceAImplInstance, is(true));
    }

    @Test(expected = CycleDependencyException.class)
    public void should_throw_exception_when_cycle_dependency_happens() {
        container.addComponent(IA.class, IAImpl_Cycle_Dependent.class);
        container.getComponent(IA.class);
    }

    @Test(expected = CycleDependencyException.class)
    public void should_throw_exception_when_cycle_dependency_happens_in_two_classes() {
        container.addComponent(IA.class, IA_ImplDependsOn_IB.class);
        container.addComponent(IB.class, IB_ImplDependsOn_IA.class);
        container.getComponent(IA.class);
    }

    @Test
    public void should_use_setter_injection_to_inject_implementation() {
        container.addComponent(IA.class, IAImpl.class);
        IA_As_SetterArg instance = container.getComponent(IA_As_SetterArg.class);

        assertThat(instance.getIaImplInstance(), is(IAImpl.class));
    }

    @Test
    public void should_use_setter_injection_to_inject_instance() {
        IAImpl iaImplInstance = new IAImpl();
        container.addComponent(IA.class, iaImplInstance);
        IA_As_SetterArg instance = container.getComponent(IA_As_SetterArg.class);

        assertThat(instance.getIaImplInstance() == iaImplInstance, is(true));
    }

    @Test
    public void should_get_dependency_from_parent_container() {
        ExpressContainer parentContainer = new ExpressContainer();
        parentContainer.addComponent(IA.class, IAImpl.class);
        container.setParent(parentContainer);

        IA_As_ConstructorArg instance = container.getComponent(IA_As_ConstructorArg.class);
        assertThat(instance.getInterfaceA(), is(IAImpl.class));
    }

    //TODO: how to fix set again after construction.
    //TODO: make a cache, so a second get can get a previously created object
    //TODO: init container with package name, and init with annotations in specified package.
}
