import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public abstract class Gate {

    int out;

    abstract List<int[]> toBoolean(int tick);

}
