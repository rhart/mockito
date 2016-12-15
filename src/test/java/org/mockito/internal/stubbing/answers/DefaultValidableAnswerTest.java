/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.answers;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.WrongTypeOfReturnValue;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.mock.MockCreationSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class DefaultValidableAnswerTest {

    @Test
    public void should_fail_if_returned_value_of_answer_is_incompatible_with_return_type() throws Throwable {
        // given
        class AWrongType { }
        MockCreationSettings mock_settings_with_default_answer = (MockCreationSettings) Mockito.withSettings().defaultAnswer(new Returns(new AWrongType()));
        try {
            // when
            new DefaultValidableAnswer(mock_settings_with_default_answer.getDefaultAnswer())
                    .validatedAnswerFor(new InvocationBuilder().method("toString").toInvocation());
            fail("expected validation to fail");
        } catch (WrongTypeOfReturnValue e) {
            // then
            assertThat(e.getMessage())
                    .containsIgnoringCase("Default answer returned a result with the wrong type")
                    .containsIgnoringCase("AWrongType cannot be returned by toString()")
                    .containsIgnoringCase("toString() should return String");
        }
    }

    @Test
    public void should_not_fail_if_returned_value_of_answer_is_null() throws Throwable {
        new DefaultValidableAnswer(new Returns(null)).validatedAnswerFor(
                new InvocationBuilder().method("toString").toInvocation()
        );
    }
}
