import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public class Xor extends Gate {

    final static int MAX_NUMBER_OF_BITS_IN_STREAM = 100; // max 99 bits in stream

    int in1;

    int in2;

    Xor(int in1, int in2, int out) {
        this.in1 = in1;
        this.in2 = in2;
        this.out = out;
        this.id = in1 + "" + in2 + "" + out + "";
    }


    List<int[]> toBoolean(int tick) {

        // kodiere die eingänge: 101 = variable 1, bit nummer 1, 102, bit nummer 2 in stream

        int maxTicks = MAX_NUMBER_OF_BITS_IN_STREAM;

        int[] clause1 = new int[] {
                (in1 * maxTicks + tick) * -1, (in2 * maxTicks + tick) * -1, (out * maxTicks + tick) * -1
        };

        int[] clause2 = new int[] {
                in1 * maxTicks + tick, in2 * maxTicks + tick, (out * maxTicks + tick) * -1
        };

        int[] clause3 = new int[] {
                in1 * maxTicks + tick, (in2 * maxTicks + tick) * -1, out * maxTicks + tick
        };

        int[] clause4 = new int[] {
                (in1 * maxTicks + tick) * -1, in2 * maxTicks + tick, out * maxTicks + tick
        };

        return new ArrayList<>(Arrays.asList(clause1, clause2, clause3, clause4));
    }

}
