package io.github.githubjakob.convolutionalSat.logic;

import java.util.List;

/**
 * Anything that implements the toCnf() Method.
 *
 */
public interface Property {
    List<Clause> toCnf();
}
