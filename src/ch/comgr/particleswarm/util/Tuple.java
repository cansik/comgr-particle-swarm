package ch.comgr.particleswarm.util;

/**
 * Immutable tupel to store a value pair.
 * @param <T> Type of the first parameter in the tuple.
 * @param <K> Type of the second parameter in the tuple.
 */
public class Tuple<T, K> {
    private final T first;
    private final K second;

    /**
     * Creates a new tuple with two values
     * @param first Value of the first tuple parameter.
     * @param second Value of the second tuple parameter.
     */
    public Tuple(T first, K second)
    {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first tuple value.
     * @return Returns the first tuple value.
     */
    public T getFirst() {
        return first;
    }

    /**
     * Returns the second tuple value.
     * @return Returns the second tuple value.
     */
    public K getSecond() {
        return second;
    }
}
