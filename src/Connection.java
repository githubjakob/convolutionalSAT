import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by jakob on 07.06.18.
 */
public abstract class Connection {

    Gate from;

    List<Gate> to;

    Connection(Gate from, Gate... to) {
        this.from = from;
        this.to = Arrays.asList(to);
    }

}
