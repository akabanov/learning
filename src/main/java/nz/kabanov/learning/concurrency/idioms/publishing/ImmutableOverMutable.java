package nz.kabanov.learning.concurrency.idioms.publishing;

import java.util.HashSet;
import java.util.Set;

/**
 * Immutable if:
 * 1. state can't be modified after construction
 * 2. all it's fields are 'final'
 * 3. properly constructed: 'this' doesn't escape during construction
 */
public class ImmutableOverMutable {

    private final Set<String> stooges = new HashSet<>();

    public ImmutableOverMutable() {
        stooges.add("Moe");
        stooges.add("Larry");
        stooges.add("Curly");
    }

    public boolean isStooge(String name) {
        return stooges.contains(name);
    }
}
