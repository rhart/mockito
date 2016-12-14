/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.answers;

import org.junit.Test;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class ReturnsArgumentAtTest {
    @Test
    public void should_be_able_to_return_the_first_parameter() throws Throwable {
        assertThat(new ReturnsArgumentAt(0).answer(invocationWith("A", "B"))).isEqualTo("A");
    }

    @Test
    public void should_be_able_to_return_the_second_parameter()
            throws Throwable {
        assertThat(new ReturnsArgumentAt(1).answer(invocationWith("A", "B", "C"))).isEqualTo("B");
    }

    @Test
    public void should_be_able_to_return_the_last_parameter() throws Throwable {
        assertThat(new ReturnsArgumentAt(-1).answer(invocationWith("A"))).isEqualTo("A");
        assertThat(new ReturnsArgumentAt(-1).answer(invocationWith("A", "B"))).isEqualTo("B");
    }

    @Test
    public void should_be_able_to_return_the_specified_parameter() throws Throwable {
        assertThat(new ReturnsArgumentAt(0).answer(invocationWith("A", "B", "C"))).isEqualTo("A");
        assertThat(new ReturnsArgumentAt(1).answer(invocationWith("A", "B", "C"))).isEqualTo("B");
        assertThat(new ReturnsArgumentAt(2).answer(invocationWith("A", "B", "C"))).isEqualTo("C");
    }

    @Test
    public void should_identify_argument_type_of_parameters() throws Exception {
        assertThat(new ReturnsArgumentAt(1).returnedTypeOnSignature(new InvocationBuilder().method("varargsReturningString")
                                                                                           .argTypes(Object[].class)
                                                                                           .args(new Object(), new Object(), new Object()).toInvocation())).isEqualTo(Object.class);
        assertThat(new ReturnsArgumentAt(0).returnedTypeOnSignature(new InvocationBuilder().method("oneArray")
                                                                                           .argTypes(boolean[].class)
                                                                                           .args(true, false, false).toInvocation())).isEqualTo(boolean[].class);
        assertThat(new ReturnsArgumentAt(0).returnedTypeOnSignature(new InvocationBuilder().method("mixedVarargsReturningString")
                                                                                           .argTypes(Object.class, String[].class)
                                                                                           .args(new Object(), new String[]{"A", "B", "C"}).toInvocation())).isEqualTo(Object.class);
        assertThat(new ReturnsArgumentAt(1).returnedTypeOnSignature(new InvocationBuilder().method("mixedVarargsReturningString")
                                                                                           .argTypes(Object.class, String[].class)
                                                                                           .args(new Object(), new String[]{"A", "B", "C"}).toInvocation())).isEqualTo(String.class);
    }

    @Test
    public void should_handle_returning_vararg_as_array() throws Throwable {
        Invocation mixedVarargsReturningStringArray = new InvocationBuilder().method("mixedVarargsReturningStringArray")
                                                                             .argTypes(Object.class, String[].class)
                                                                             .args(new Object(), new String[]{"A", "B", "C"}).toInvocation();
        assertThat(new ReturnsArgumentAt(1).returnedTypeOnSignature(mixedVarargsReturningStringArray)).isEqualTo(String[].class);
        assertThat(new ReturnsArgumentAt(1).answer(mixedVarargsReturningStringArray)).isEqualTo(new String[]{"A", "B", "C"});
        Invocation mixedVarargsReturningObjectArray = new InvocationBuilder().method("mixedVarargsReturningStringArray")
                                                                             .argTypes(Object.class, String[].class)
                                                                             .args(new Object(), new String[]{"A", "B", "C"}).toInvocation();
        assertThat(new ReturnsArgumentAt(1).returnedTypeOnSignature(mixedVarargsReturningObjectArray)).isEqualTo(String[].class);
        assertThat(new ReturnsArgumentAt(1).answer(mixedVarargsReturningObjectArray)).isEqualTo(new String[]{"A", "B", "C"});
    }

    @Test
    public void should_raise_an_exception_if_index_is_not_in_allowed_range_at_creation_time() throws Throwable {
        try {
            new ReturnsArgumentAt(-30);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage())
                    .containsIgnoringCase("argument index")
                    .containsIgnoringCase("positive number")
                    .contains("1")
                    .containsIgnoringCase("last argument");
        }
    }

    private static InvocationOnMock invocationWith(Object... parameters) {
        return new InvocationBuilder().method("varargsReturningString")
                                      .argTypes(Object[].class)
                                      .args(parameters).toInvocation();
    }

}
