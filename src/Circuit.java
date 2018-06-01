import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import java.util.*;

/**
 * Created by jakob on 31.05.18.
 */
public class Circuit {

    // ENDOCING FOR LITERALS

    final static int GLOBAL_INPUT = 1;
    final static int GLOBAL_OUTPUT = 2;

    // Ranges
    final static int INPUT = 100;
    final static int OUTPUT = 200;

    final static int CONNECTIONS = 1000; //xxYY

    int[] input;

    int[] output;

    ArrayList<Gate> gates = new ArrayList<>();

    int idCounter = 3;

    HashSet<Integer> ins = new HashSet<>();

    HashSet<Integer> outs = new HashSet<>();

    SetMultimap<Integer, Integer> connectionsOutToIn = HashMultimap.create();

    SetMultimap<Integer, Integer> connectionsInToOut = HashMultimap.create();

    Circuit(int[] input, int[] output) {
        this.input = input;
        this.output = output;

        //output

        ins.add(GLOBAL_OUTPUT);
        outs.add(GLOBAL_INPUT);
    }

    void addGate(Gate gate) {
        gates.add(gate);
    }

    void add(String type) {

        if (type == "register") {
            int in = idCounter;
            idCounter++;
            int out = idCounter;
            idCounter++;

            ins.add(in);
            outs.add(out);

            connectionsOutToIn.put(GLOBAL_INPUT, in);
            connectionsInToOut.put(in, GLOBAL_INPUT);

            connectionsOutToIn.put(out, GLOBAL_OUTPUT);
            connectionsInToOut.put(GLOBAL_OUTPUT, out);

            outs.forEach((outId) -> {
                if (outId != in && outId != in && outId != out) {
                    connectionsOutToIn.put(outId, in);
                    connectionsInToOut.put(in, outId);
                }
            });

            ins.forEach((inId) -> {
                if (inId != out && inId != in) {
                    connectionsOutToIn.put(out, inId);
                    connectionsInToOut.put(inId, out);
                }
            });

            gates.add(new Register(in, out));

            System.out.println("Created Register with in: " + in + " out: " + out);

        }

        if (type == "xor") {
            int in1 = idCounter;
            idCounter++;
            int in2 = idCounter;
            idCounter++;
            int out = idCounter;
            idCounter++;

            ins.add(in1);
            ins.add(in2);

            outs.add(out);
            // create possible connectionsOutToIn

            //zwischen input und gate-eingang
            connectionsOutToIn.put(GLOBAL_INPUT, in1);
            connectionsOutToIn.put(GLOBAL_INPUT, in2);
            connectionsInToOut.put(in1, GLOBAL_INPUT);
            connectionsInToOut.put(in2, GLOBAL_INPUT);

            outs.forEach((outId) -> {
                if (outId != in1 && outId != in2 && outId != out) {
                    connectionsOutToIn.put(outId, in1);
                    connectionsOutToIn.put(outId, in2);
                    connectionsInToOut.put(in1, outId);
                    connectionsInToOut.put(in2, outId);
                }
            });

            ins.forEach((inId) -> {
                if (inId != out && inId != in1 && inId != in2) {
                    connectionsOutToIn.put(out, inId);
                    connectionsInToOut.put(inId, out);
                }
            });

            gates.add(new Xor(in1, in2, out));

            System.out.println("Created xor with in1: " + in1 + ", in2: " + in2 + ", out: " + out);

        }

    };

    List<int[]> toBoolean() {
        List<int[]> allClauses = new ArrayList<>();

        // Für jeden Tick:
        for (int i = 1; i <= input.length; i++) {
            final int bitIndex = i;

            // die input und output bits als klauseln
            boolean bitInput = input[i-1] == 1;
            boolean bitOutput = output[i-1] == 1;

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
                System.out.println("Every out needs to have at least one in: " + Main.reader.decode(connections) );
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
                System.out.println("Every in need to have at least one out: " + Main.reader.decode(connections) );
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
                        System.out.println("For every connection, exclude the other: " + Main.reader.decode(excludes) );
                        allClauses.add(excludes);

                    }
                }

                // and every out needs to have a in

            }



            // Gates
            // die logik der gates als klauseln
            for (Gate gate : gates) {
                if (gate instanceof And) {
                    List<int[]> clauses = gate.toBoolean(i);
                    allClauses.addAll(clauses);
                    System.out.println("clause for gate AND created for bit numer " + i);
                }

                if (gate instanceof Xor) {
                    List<int[]> clauses = gate.toBoolean(i);
                    allClauses.addAll(clauses);
                    System.out.println("clause for gate Xor created for bit numer " + i);
                }

                if (gate instanceof Register) {
                    List<int[]> clauses = gate.toBoolean(i);
                    allClauses.addAll(clauses);
                    System.out.println("clause for gate REGISTER created for bit numer " + i);
                }

            }
        }
        return allClauses;

    }

}
