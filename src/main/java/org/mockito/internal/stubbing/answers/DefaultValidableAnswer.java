/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.internal.stubbing.answers;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.internal.exceptions.Reporter.wrongTypeReturnedByDefaultAnswer;

public class DefaultValidableAnswer {
    private Answer<?> defaultAnswer;

    public DefaultValidableAnswer(Answer<?> defaultAnswer) {
        this.defaultAnswer = defaultAnswer;
    }

    public Object validatedAnswerFor(InvocationOnMock invocation) throws Throwable {
        Object returnedValue = defaultAnswer.answer(invocation);

        MethodInfo methodInfo = new MethodInfo(invocation);
        if (returnedValue != null && !methodInfo.isValidReturnType(returnedValue.getClass())) {
            throw wrongTypeReturnedByDefaultAnswer(
                    invocation.getMock(),
                    methodInfo.printMethodReturnType(),
                    returnedValue.getClass().getSimpleName(),
                    methodInfo.getMethodName());
        }

        return returnedValue;
    }
}
