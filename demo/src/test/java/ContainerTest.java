package com.thoughtworks.row.ioc;

import com.thoughtworks.row.ioc.beans.*;
import com.thoughtworks.row.ioc.exception.*;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ContainerTest {

    private Container container;

    @Before
    public void setUp() throws Exception {
        container = new Container();
    }

    @Test
    public void should_be_able_to_create_instance_with_zero_constructor() {
        container.register(Service.class, ServiceImplementation.class);

        Service service = container.get(Service.class);
        assertThat(service.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Test
    public void should_be_able_to_inject_service_to_constructor() {

        container.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        container.register(Service.class, ServiceImplementation.class);

        ServiceConsumer consumer = container.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Test(expected = CyclicDependencyException.class)
    public void should_throw_exception_if_cyclic_dependency() {

        container.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        container.register(Service.class, BadService.class);

        container.get(ServiceConsumer.class);
    }

    @Test
    public void should_inject_instance_to_constructor() {

        container.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        container.register(Service.class, PrivateService.getInstance());

        ServiceConsumer consumer = container.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(PrivateService.class.getCanonicalName()));
    }

    @Test
    public void should_be_able_to_declare_service_lifecycle() {
        container.register(Service.class, ServiceImplementation.class, Lifecycle.Singleton);
        container.register(ServiceConsumer.class, TransientServiceConsumer.class, Lifecycle.Transient);

        TransientServiceConsumer first = (TransientServiceConsumer) container.get(ServiceConsumer.class);
        TransientServiceConsumer second = (TransientServiceConsumer) container.get(ServiceConsumer.class);

        assertThat(first, not(sameInstance(second)));
        assertThat(first.getService(), sameInstance(second.getService()));
    }

    @Test
    public void should_be_able_to_inject_service_via_setter() {
        container.register(Service.class, ServiceImplementation.class);
        container.register(ServiceConsumer.class, SetterConsumer.class);

        ServiceConsumer consumer = container.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Test
    public void should_find_service_from_parent_container() {
        Container grandfather = new Container();
        Container father = new Container(grandfather);
        Container son = new Container(father);

        grandfather.register(Service.class, ServiceImplementation.class);
        son.register(ServiceConsumer.class, SetterConsumer.class);

        ServiceConsumer consumer = son.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Test(expected = ComponentNotFoundException.class)
    public void should_throw_component_not_found_exception() {
        container.register(ServiceConsumer.class, SetterConsumer.class);
        container.get(Service.class);
    }

}
