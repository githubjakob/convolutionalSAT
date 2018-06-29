package io.github.githubjakob.convolutionalSat;


import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;

import java.util.*;

/**
 * Created by jakob on 31.05.18.
 */
public class Problem {

    @Getter
    private final int numberOfBitStreams;

    @Getter
    private int numberOfBits;

    private List<Module> modules;

    public Problem(List<Module> modules, int numberOfBits, int numberOfBitStreams) {
        this.numberOfBits = numberOfBits;
        this.modules = modules;
        this.numberOfBitStreams = numberOfBitStreams;
    }

    public List<Clause> convertProblemToCnf() {
        List<Clause> cnf = new ArrayList<>();

        for (Module module : modules) {
            List<Clause> clauses = module.convertModuleToCnf();
            cnf.addAll(clauses);
        }

        return cnf;

    }

    public List<Gate> getGates() {
        List<Gate> allGates = new ArrayList<>();

        for (Module module : modules) {
            List<Gate> gatesFromModule = module.getGates();
            allGates.addAll(gatesFromModule);
        }

        return allGates;
    }

    public List<Connection> getConnections() {
        List<Connection> allGates = new ArrayList<>();

        for (Module module : modules) {
            List<Connection> connectionsFromModule = module.getConnections();
            allGates.addAll(connectionsFromModule);
        }

        return allGates;
    }
}
