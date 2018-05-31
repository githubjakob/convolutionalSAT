import com.sun.org.apache.regexp.internal.RE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public class Circuit {

    int[] input;

    ArrayList<Gate> gates = new ArrayList<>();

    Circuit(int[] input) {
        this.input = input;
    }

    void addGate(Gate gate) {
        gates.add(gate);
    }

    List<int[]> toBoolean() {
        List<int[]> allClauses = new ArrayList<>();
        for (int i = 1; i <= input.length; i++) {
            boolean bit = input[i-1] == 1;

            //input bit id 1


            int inputBit = bit ? 100 + i : (100 + i ) * -1;
            allClauses.add(new int[] {
                    inputBit
            });


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
