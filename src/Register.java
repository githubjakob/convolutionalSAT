import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public class Register extends Gate {

    int in;

    Register(int in, int out) {
        this.in = in;
        this.out = out;
        this.id = in + "" + out + "";
    }

    @Override
    List<int[]> toBoolean(int tick) {



        if (tick == 1) { // first bit from register is false
            List<int[]> clauses = new ArrayList<>();
            int[] clause = new int[] {
                (out * 100 + tick) *-1
            };
            clauses.add(clause);
            return clauses ;
        } else {
           /*
          Bedingungen:
           1. 22 => 11 (wenn output zu tick 2 true, dann muss input 1 zu tick 1 wahr sein)
           2. ~22 => ~11 (wenn output zu tick 2 false, dann muss input 1 zu tick 1 false sein)

           umformung ergibt
           (~22 v 11) u (22 v ~11)


            */
           int previousTick = tick-1;
            List<int[]> clauses = new ArrayList<>();
            int[] clause1 = new int[] {
                    (out * 100 + tick) * -1,
                    in * 100 + previousTick
            };
            int[] clause2 = new int[] {
                    out * 100 + tick,
                    (in * 100 + previousTick) * -1
            };
            clauses.add(clause1);
            clauses.add(clause2);
            return clauses ;
        }
    }
}
