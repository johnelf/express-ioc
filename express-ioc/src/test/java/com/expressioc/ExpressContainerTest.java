package com.expressioc;

import com.expressioc.exception.AssembleComponentFailedException;
import com.expressioc.exception.CycleDependencyException;
import com.expressioc.parameters.ConstParameter;
import com.expressioc.test.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ExpressContainerTest {

    private ExpressContainer container;

    @Before
    public void setUp() {
        container = new ExpressContainer();
    }

    @Test
    public void should_get_instance_by_default_constructor() {
        ClassWithDefaultConstructor instance = container.getComponent(ClassWithDefaultConstructor.class);
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
    public void should_get_instance_by_constructor_with_constant_parameters() {
        container.addComponent(IB.class,
                            IBImplWithConstantConstructorArgs.class,
                            new ConstParameter("MyName"),
                            new ConstParameter("100"),
                            new ConstParameter("99999999"));

        IB instance = container.getComponent(IB.class);
        IBImplWithConstantConstructorArgs concreteInstance = (IBImplWithConstantConstructorArgs)instance;

        assertThat(concreteInstance.getName(), is("MyName"));
        assertThat(concreteInstance.getAge(), is(100));
        assertThat(concreteInstance.getDistance(), is(99999999L));
    }

    @Test
    public void should_get_instance_by_constructor_with_hybrid_parameters() {
        container.addComponent(IA.class, IAImpl.class);
        container.addComponent(IB.class,
                            IBImplWithConstantConstructorArgs.class,
                            new ConstParameter("MyName"), null/* will auto_resolve_to IAImpl.class instance*/);

        IB instance = container.getComponent(IB.class);
        IBImplWithConstantConstructorArgs concreteInstance = (IBImplWithConstantConstructorArgs)instance;

        assertThat(concreteInstance.getName(), is("MyName"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_inject_instance_with_wrong_class() {
        container.addComponent(IA.class, new Object());
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

        container = new ExpressContainer(parentContainer);

        IA_As_ConstructorArg instance = container.getComponent(IA_As_ConstructorArg.class);
        assertThat(instance.getInterfaceA(), is(IAImpl.class));
    }

    @Test(expected = AssembleComponentFailedException.class)
    public void should_throw_assemble_exception_when_dependency_not_exist_in_parent_container() {
        ExpressContainer emptyParentContainer = new ExpressContainer();
        ExpressContainer container = new ExpressContainer(emptyParentContainer);

        container.getComponent(IA_As_ConstructorArg.class);
    }

    @Test
    public void should_able_to_find_single_implementation_class_of_interface_by_default() {
        ExpressContainer container = new ExpressContainer("com.expressioc.test");
        IC_As_ConstructorArg instance = container.getComponent(IC_As_ConstructorArg.class);
        assertThat(instance.getIcInstance() instanceof ICImpl, is(true));
    }

    @Test(expected = AssembleComponentFailedException.class)
    public void should_failed_when_find_more_than_one_implementation_when_auto_find_implementation() {
        ExpressContainer container = new ExpressContainer("com.expressioc.test");
        container.getComponent(IA_As_ConstructorArg.class);
    }

    @Test
    public void should_able_to_correctly_load_implementation_objects_list() {
        ExpressContainer container = new ExpressContainer("com.expressioc.test");

        List<? extends IC> preLoadedICs = container.getImplementationObjectListOf(IC.class);

        assertThat(preLoadedICs.size(), is(2));

        Class<? extends IC> clazz0 = preLoadedICs.get(0).getClass();
        Class<? extends IC> clazz1 = preLoadedICs.get(1).getClass();

        assertThat(clazz0.equals(clazz1), is(false));
        assertThat(clazz0.equals(ICImpl.class) || clazz0.equals(ICImpl2.class), is(true));
        assertThat(clazz1.equals(ICImpl.class) || clazz1.equals(ICImpl2.class), is(true));
    }

    @Test
    public void should_cache_created_instance_of_class_tagged_with_Singleton_annotation() {
        ExpressContainer container = new ExpressContainer("com.expressioc.test");
        ICImpl a = container.getComponent(ICImpl.class);
        ICImpl b = container.getComponent(ICImpl.class);

        assertThat(a != null && a == b, is(true));
    }

    @Test
    public void should_get_the_container_in_which_instance_located_when_instance_is_ContainerAware() {
        ExpressContainer container = new ExpressContainer("com.expressioc.test");

        ICImpl2 containAwareObject = container.getComponent(ICImpl2.class);
        assertThat(containAwareObject.getWhichContainerIAmIn() == container, is(true));
    }
}
