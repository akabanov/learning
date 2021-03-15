package nz.kabanov.learning.concurrency.idioms.cache;

import java.util.concurrent.*;
import java.util.function.Function;

public class FutureCache<T, R> implements Function<T, R> {

    private final ConcurrentMap<T, Future<R>> map = new ConcurrentHashMap<>();
    private final Function<T, R> function;

    public FutureCache(Function<T, R> function) {
        this.function = function;
    }

    @Override
    public R apply(T t) {
        while (true) {
            Future<R> future = map.computeIfAbsent(t, x -> new FutureTask<>(() -> function.apply(x)));
            try {
                return future.get();
            } catch (CancellationException e) {
                map.remove(t);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
