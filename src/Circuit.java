import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.Var;
import components.*;
import components.Connection;
import components.InputPin;
import logic.Clause;
import logic.Clauses;
import logic.TimeDependentVariable;
import logic.Variable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.sat4j.core.VecInt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jakob on 31.05.18.
 */
public class Circuit {

    int[] inputBitStream;

    int[] outputBitStream;

    Set<Gate> gates = new HashSet<>();

    private Set<Connection> connections = new HashSet<>();

    private Set<InputPin> inputPins = new HashSet<>();

    private Set<OutputPin> outputPins = new HashSet<>();

    Input globalInput = new Input();

    Output globalOutput = new Output();

    Circuit() {
        gates.add(globalInput);
        gates.add(globalOutput);

        inputPins.addAll(globalOutput.getInputPins());
        outputPins.add(globalInput.getOutputPin());
    }

    void addInputBitStream(int[] input) {
        this.inputBitStream = input;
    }

    void addOutputBitStream(int[] output) {
        this.outputBitStream = output;
    }

    Set<Connection> getConnections() {
        return this.connections;
    }

    Register addRegister() {
        Register register = new Register();
        gates.add(register);
        inputPins.addAll(register.getInputPins());
        outputPins.add(register.getOutputPin());
        createNewConnectionsFor(register);
        return register;
    };

    Xor addXor() {
        Xor xor = new Xor();
        gates.add(xor);
        inputPins.addAll(xor.getInputPins());
        outputPins.add(xor.getOutputPin());
        createNewConnectionsFor(xor);
        return xor;
    };

    private void createNewConnectionsFor(Gate justCreated) {
        for (Gate gate : getAllComponentsWithOutputs()) {
            if (gate.equals(justCreated)) {
                continue;
            }

            for (InputPin xorInputPin : justCreated.getInputPins()) {
                connections.add(new Connection(gate.getOutputPin(), xorInputPin));
            }
        }

        for (Gate gate : getAllComponentsWithInputs()) {
            if (gate.equals(justCreated)) {
                continue;
            }

            for (InputPin componentInputPin : gate.getInputPins()) {
                connections.add(new Connection(justCreated.getOutputPin(), componentInputPin));
            }
        }
    }

    private Clauses convertCircuitToCnfForTick(int tick) {
        Clauses clausesForTick = new Clauses(tick);

        //für jede Verbindung
        for (Connection connection : connections) {
            clausesForTick.addAllClauses(connection.convertToCnfAtTick(tick));
        }

        // für jedes Bauteil
        for (Gate gate : gates) {
            clausesForTick.addAllClauses(gate.convertToCnfAtTick(tick));
        }

        // für jedes bit
        boolean inputBit = inputBitStream[tick] == 1;
        Clause inputClause = new Clause(new TimeDependentVariable(tick, inputBit, globalInput));
        clausesForTick.addClause(inputClause);

        boolean outputBit = outputBitStream[tick] == 1;
        Clause outputClause = new Clause(new TimeDependentVariable(tick, outputBit, globalOutput));
        clausesForTick.addClause(outputClause);

        // für jeden Output pin
        for (OutputPin outputPin : outputPins) {
            Clause possibleConnections = new Clause();
            for (Connection connection : connections) {
                if (connection.getFrom().equals(outputPin)) {
                    possibleConnections.addVariable(new Variable(true, connection));
                }
            }
            clausesForTick.addClause(possibleConnections);
        }

        // für jeden Input pin

        for (InputPin inputPin : inputPins) {

            List<Connection> connectionsWithSameTo = connections.stream().filter(connection -> connection.getTo().equals(inputPin)).collect(Collectors.toList());

            Clause possibleConnections = new Clause();
            for (Connection connection : connectionsWithSameTo) {
                possibleConnections.addVariable(new Variable(true, connection));

            }
            clausesForTick.addClause(possibleConnections);


            for (Connection connection : connectionsWithSameTo) {
                for (Connection other : connectionsWithSameTo) {
                    if (connection.equals(other)) {
                        continue;
                    }
                    Variable connectionFalse = new Variable(false, connection);
                    Variable otherFalse = new Variable(false, other);
                    Clause exclude = new Clause(connectionFalse, otherFalse);
                    clausesForTick.addClause(exclude);
                }

            }
        }


        return clausesForTick;
    }

    public List<Clauses> convertCircuitToCnf() {
        List<Clauses> allClauses = new ArrayList<>();

        for (int tick = 0; tick < inputBitStream.length; tick++) {
            allClauses.add(convertCircuitToCnfForTick(tick));
        }

        return allClauses;
    }

    /*
    List<int[]> toBoolean() {
        List<int[]> allClauses = new ArrayList<>();

        // Für jeden Tick:
        for (int i = 1; i <= inputBitStream.length; i++) {
            final int bitIndex = i;

            // die inputBitStream und outputBitStream bits als klauseln
            boolean bitInput = inputBitStream[i-1] == 1;
            boolean bitOutput = outputBitStream[i-1] == 1;

            int inputBit = bitInput ? INPUT + i : (INPUT + i ) * -1;
            int outputBit = bitOutput ? OUTPUT + i : (OUTPUT + i ) * -1;

            allClauses.add(new int[] {
                    inputBit
            });
            allClauses.add(new int[] {
                    outputBit
            });

            // Für alle Verbindungen, ein/ausgänge der verbindung müssen gleich sind
            connectionsOutToIn.forEach((out, in) -> {
                int connection = out * CONNECTIONS + in;
                allClauses.add(new int[] {
                        connection * -1, in * INPUT + bitIndex, (out * INPUT + bitIndex) * -1
                });
                allClauses.add(new int[] {
                        connection * -1, (in * INPUT + bitIndex) * -1, out * INPUT + bitIndex
                });
            });

            // every out need to have at least one in
            for (Integer out : connectionsOutToIn.keySet()) {
                Set<Integer> ins = connectionsOutToIn.get(out);

                int[] connections = new int[ins.size()];

                // klausel: 600001 oder 600002
                int index = 0;
                for (Integer in : ins) {
                    int connection = out * CONNECTIONS + in;
                    connections[index] = connection;
                    index++;
                };
                System.out.println("Every out needs to have at least one in: " + BooleanExpression.reader.decode(connections) );
                allClauses.add(connections);
            }

            // every in need to have a connected out
            for (Integer in : connectionsInToOut.keySet()) {
                Set<Integer> outs = connectionsInToOut.get(in);

                int[] connections = new int[outs.size()];

                // klausel: 100002 oder 600002
                int index = 0;
                for (Integer out : outs) {
                    int connection = out * CONNECTIONS + in;
                    connections[index] = connection;
                    index++;
                };
                System.out.println("Every in need to have at least one out: " + BooleanExpression.reader.decode(connections) );
                allClauses.add(connections);

                //für jede connection sind die anderen ausgeschlossen
                for (int connection : connections) {

                    for (int other : connections) {
                        if (other == connection) {
                            continue;
                        }
                        int[] excludes = new int[]{
                                connection * -1, other * -1
                        };
                        System.out.println("For every connection, exclude the other: " + BooleanExpression.reader.decode(excludes) );
                        allClauses.add(excludes);

                    }
                }
            }


            // Gates
            // die logik der gates als klauseln
            for (Component gate : gates) {
                if (gate instanceof Xor) {
                    List<int[]> clauses = ((Xor) gate).toBoolean(i);
                    allClauses.addAll(clauses);
                    System.out.println("clause for gate Xor created for bit numer " + i);
                }

                if (gate instanceof Register) {
                    List<int[]> clauses = ((Register) gate).toBoolean(i);
                    allClauses.addAll(clauses);
                    System.out.println("clause for gate REGISTER created for bit numer " + i);
                }

            }
        }
        return allClauses;

    }

    Gate getGateByInput(int in) {
        for (Gate gate : gates) {
            if (gate instanceof Xor) {
                if (((Xor) gate).in1 == in || ((Xor) gate).in2 == in) {
                    return (Xor) gate;
                }
            }
            if (gate instanceof Register) {
                if (((Register) gate).in == in) {
                    return (Register) gate;
                }
            }
        }
        return null;
    }

    Gate getGateByOutput(int out) {
        for (Gate gate : gates) {
            if (gate instanceof Gate) {
                if (((Gate) gate).out == out) {

                        return (Gate) gate;
                }
            }
        }
        return null;
    }*/

    public List<Gate> getAllComponentsWithOutputs() {
        return gates.stream().filter(gate -> !(gate instanceof Output)).collect(Collectors.toList());
    }

    public List<Gate> getAllComponentsWithInputs() {
        return gates.stream().filter(gate -> !(gate instanceof Input)).collect(Collectors.toList());
    }
}
