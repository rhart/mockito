/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.answers;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.internal.exceptions.Reporter.invalidArgumentPositionRangeAtInvocationTime;
import static org.mockito.internal.exceptions.Reporter.invalidArgumentRangeAtIdentityAnswerCreationTime;

/**
 * Returns the passed parameter identity at specified index.
 * <p>
 * <p>The <code>argumentIndex</code> represents the index in the argument array of the invocation.</p>
 * <p>If this number equals -1 then the last argument is returned.</p>
 *
 * @see org.mockito.AdditionalAnswers
 * @since 1.9.5
 */
public class ReturnsArgumentAt implements Answer<Object>, Serializable {

    private static final long serialVersionUID = -589315085166295101L;

    public static final int LAST_ARGUMENT = -1;

    private final int wantedArgumentPosition;

    /**
     * Build the identity answer to return the argument at the given position in the argument array.
     *
     * @param wantedArgumentPosition The position of the argument identity to return in the invocation.
     *                               Using <code>-1</code> indicates the last argument.
     */
    public ReturnsArgumentAt(int wantedArgumentPosition) {
        this.wantedArgumentPosition = checkWithinAllowedRange(wantedArgumentPosition);
    }

    public Object answer(InvocationOnMock invocation) throws Throwable {
        validateIndexWithinInvocationRange(invocation);

        int actualArgumentPosition = actualArgumentPosition(invocation);
        if (wantedArgIsVarargAndIsCompatibleWithReturnType(invocation.getMethod(), actualArgumentPosition)) {
            return ((Invocation) invocation).getRawArguments()[actualArgumentPosition];
        }
        return invocation.getArgument(actualArgumentPosition);
    }

    private boolean wantedArgIsVarargAndIsCompatibleWithReturnType(Method method, int actualArgumentPosition) {
        int varargPosition = method.getParameterTypes().length - 1;
        return method.isVarArgs() && actualArgumentPosition == varargPosition;
    }

    private int actualArgumentPosition(InvocationOnMock invocation) {
        return returningLastArg() ?
               lastArgumentIndexOf(invocation) :
               argumentIndexOf(invocation);
    }

    private boolean returningLastArg() {
        return wantedArgumentPosition == LAST_ARGUMENT;
    }

    private int argumentIndexOf(InvocationOnMock invocation) {
        return wantedArgumentPosition;
    }

    private int lastArgumentIndexOf(InvocationOnMock invocation) {
        return invocation.getArguments().length - 1;
    }

    private int checkWithinAllowedRange(int argumentPosition) {
        if (argumentPosition != LAST_ARGUMENT && argumentPosition < 0) {
            throw invalidArgumentRangeAtIdentityAnswerCreationTime();
        }
        return argumentPosition;
    }

    public int wantedArgumentPosition() {
        return wantedArgumentPosition;
    }

    public void validateIndexWithinInvocationRange(InvocationOnMock invocation) {
        if (!argumentPositionInRange(invocation)) {
            throw invalidArgumentPositionRangeAtInvocationTime(invocation,
                                                               returningLastArg(),
                                                               wantedArgumentPosition);
        }
    }

    private boolean argumentPositionInRange(InvocationOnMock invocation) {
        int actualArgumentPosition = actualArgumentPosition(invocation);
        if (actualArgumentPosition < 0) {
            return false;
        }
        if (!invocation.getMethod().isVarArgs()) {
            return invocation.getArguments().length > actualArgumentPosition;
        }
        // for all varargs accepts positive ranges
        return true;
    }

    public Class<?> returnedTypeOnSignature(InvocationOnMock invocation) {
        int actualArgumentPosition = actualArgumentPosition(invocation);

        Class<?>[] parameterTypes = invocation.getMethod().getParameterTypes();
        if (!invocation.getMethod().isVarArgs()) {
            return parameterTypes[actualArgumentPosition];
        }

        int varargPosition = parameterTypes.length - 1;

        if (actualArgumentPosition < varargPosition) {
            return parameterTypes[actualArgumentPosition];
        } else {
            // try the vararg array type
            if (actualArgumentPosition == varargPosition
                && invocation.getMethod().getReturnType().isAssignableFrom(parameterTypes[actualArgumentPosition])) {
                return parameterTypes[actualArgumentPosition]; // move to MethodInfo ?
            }
            return parameterTypes[varargPosition].getComponentType();
        }
    }
}
