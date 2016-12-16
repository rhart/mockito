/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.stubbing;

import org.mockito.Incubating;
import org.mockito.invocation.InvocationOnMock;

/**
 * Allow to validate this answer is correct for the invocation.
 *
 * <p>
 * Suppose there's If the code one was to write an answer like
 *
 * <pre class="code"><code class="java">
 * when(mock.someMethod(anyString())).then(doSomethingTricky());
 *
 * static Answer doSomethingTricky() {
 *     return new Answer() {
 *         Object answer(InvocationOnMock invocation) {
 *             // tricky stuff
 *         }
 *     });
 * }
 * </code></pre>
 * </p>
 *
 * <p>
 * Then later there's some refactoring but the answer is not anymore compatible, instead of having
 * an exception raised later at <em>execution time</em>, one can make this answer validable at <em>stub time</em> by
 * implementing this contract.
 *
 * <pre class="code"><code class="java">
 * when(mock.someMethod(anyString())).then(doSomethingTricky());
 *
 * static Answer doSomethingTricky() {
 *     return new TrickyAnswer();
 * }
 *
 * class Tricky Answer implements Answer, ValidableAnswer {
 *     public Object answer(InvocationOnMock invocation) {
 *         // tricky stuff
 *     }
 *
 *     public void validateFor(InvocationOnMock invocation) {
 *         // perform validation for this interaction
 *     }
 * }
 * </code></pre>
 * </p>
 *
 * @since 2.3.6
 */
@Incubating
public interface ValidableAnswer {

    /**
     * Perform validation of the answer at <em>stub time</em>.
     *
     * @param invocation The stubbed invocation
     *
     * @since 2.3.6
     */
    void validateFor(InvocationOnMock invocation);

}
