import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public class Register extends Gate {

    int in;

    int out;

    int savedBit = 0; // initialize register with bit 0

    Register(int in, int out) {
        this.in = in;
        this.out = out;
    }

    @Override
    List<int[]> toBoolean(int tick) {
        int[] clause = new int[] {
                savedBit
        };
        return new ArrayList<>(Arrays.asList(clause));
    }
}
