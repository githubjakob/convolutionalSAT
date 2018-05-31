import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import java.util.*;

/**
 * Created by jakob on 31.05.18.
 */
public class Circuit {


    // ENDOCING FOR VARIABLES

    final static int INPUT = 1000;
    final static int OUTPUT = 5000;
    final static int CONNECTIONS = 100000; //xxxYYY

    int[] input;

    int[] output;

    ArrayList<Gate> gates = new ArrayList<>();

    int idCounter = 2;

    HashSet<Integer> ins = new HashSet<>();

    HashSet<Integer> outs = new HashSet<>();

    SetMultimap<Integer, Integer> connectionsInToOut = HashMultimap.create();

    SetMultimap<Integer, Integer> connectionsOutToIn = HashMultimap.create();

    Circuit(int[] input, int[] output) {
        this.input = input;
        this.output = output;

        //output

        ins.add(OUTPUT);
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

            connectionsInToOut.put(1, in);
            connectionsOutToIn.put(in, 1);

            outs.forEach((outId) -> {
                if (outId != in && outId != in && outId != out) {
                    connectionsInToOut.put(outId, in);
                    connectionsOutToIn.put(in, outId);
                }
            });

            ins.forEach((inId) -> {
                if (inId != out && inId != in) {
                    connectionsInToOut.put(out, inId);
                    connectionsOutToIn.put(inId, out);
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
            // create possible connectionsInToOut

            //zwischen input und gate-eingang
            connectionsInToOut.put(1, in1);
            connectionsInToOut.put(1, in2);
            connectionsOutToIn.put(in1, 1);
            connectionsOutToIn.put(in2, 1);

            outs.forEach((outId) -> {
                if (outId != in1 && outId != in2 && outId != out) {
                    connectionsInToOut.put(outId, in1);
                    connectionsInToOut.put(outId, in2);
                    connectionsOutToIn.put(in1, outId);
                    connectionsOutToIn.put(in2, outId);
                }
            });

            ins.forEach((inId) -> {
                if (inId != out && inId != in1 && inId != in2) {
                    connectionsInToOut.put(out, inId);
                    connectionsOutToIn.put(inId, out);
                }
            });

            gates.add(new Xor(in1, in2, out));

            System.out.println("Created xor with in1: " + in1 + ", in2: " + in2 + ", out: " + out);

        }

    };

    List<int[]> toBoolean() {
        List<int[]> allClauses = new ArrayList<>();
        for (int i = 1; i <= input.length; i++) {
            boolean bitInput = input[i-1] == 1;
            boolean bitOutput = output[i-1] == 1;
            final int bitIndex = i;


            // die input und output bits als klauseln
            //input bit id 1
            int inputBit = bitInput ? INPUT + i : (INPUT + i ) * -1;
            allClauses.add(new int[] {
                    inputBit
            });

            // output is id 5
            int outputBit = bitOutput ? OUTPUT + i : (OUTPUT + i ) * -1;
            allClauses.add(new int[] {
                    outputBit
            });

            // verbindung zwischen den bauteile
            // zwischen 1 und ins
            connectionsInToOut.forEach((in, out) -> {
                int connection = in * CONNECTIONS + out;
                System.out.println("connection " + connection);

                // stelle sicher, dass wenn eine verbindung gesetzt ist, die ein/ausgänge der verbindung gleich sind
                allClauses.add(new int[] {
                        connection * -1, out * 100 + 1, (in * 100 + bitIndex) * -1
                });
                allClauses.add(new int[] {
                        connection * -1, (out * 100 + 1) * -1, in * 100 + bitIndex
                });
            });

            // every in need to have a connected out
            for (Integer out : connectionsOutToIn.keySet()) {
                Set<Integer> ins = connectionsOutToIn.get(out);

                int[] connections = new int[ins.size()];

                // klausel: 100002 oder 600002
                int index = 0;
                for (Integer in : ins) {
                    int connection = in * CONNECTIONS + out;
                    System.out.println("OUT TO IN : " + connection);
                    connections[index] = connection;
                    index++;
                };
                allClauses.add(connections);

                //für jede connection sind die anderen ausgeschlossen
                for (int connection : connections) {

                    for (int other : connections) {
                        if (other == connection) {
                            continue;
                        }
                        allClauses.add(new int[] {
                                connection * -1, other * -1
                        });

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
