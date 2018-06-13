package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.Xor;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by jakob on 07.06.18.
 */
public class BasicTest {

    @Test
    public void testEquals() {
        Xor xor = new Xor();
        Xor xor2 = xor;

        assertThat(xor, is(xor2));

        assertThat(xor.inputPin1, is(xor.inputPin1));
        assertThat(xor.inputPin1, not(xor.inputPin2));

        TimeDependentVariable variable = new TimeDependentVariable(0, true, xor);
        TimeDependentVariable variable2 = new TimeDependentVariable(0, true, xor);
        TimeDependentVariable variable3 = new TimeDependentVariable(0, false, xor);
        TimeDependentVariable variable4 = new TimeDependentVariable(0, true, xor2);

        assertThat(variable, is(variable2));
        assertThat(variable, is(variable4));
        assertThat(variable2, is(variable4));
        assertFalse(variable==variable2);

        assertThat(variable, is(variable3));
    }

    @Test
    public void testHashMap() {
        Map<Variable, Integer> map = new HashMap<>();

        Xor xor = new Xor();
        Variable variable = new Variable(false, xor);
        Variable sameVariable = new Variable(false, xor);

        assertThat(variable, is(sameVariable));

        map.put(variable, 1);

        assertThat(map.containsKey(variable), is(true));
        assertThat(map.containsKey(sameVariable), is(true));


    }
}
