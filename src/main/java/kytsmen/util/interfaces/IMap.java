package kytsmen.util.interfaces;

/**
 * Associative array, map or dictionary is
 * an abstract data type composed of a
 * collection of pairs(key, value) such that
 * each possible key appears at most once
 *
 * @param <K> key
 * @param <V> value
 * @author dkytsmen
 */

public interface IMap<K, V> {

    /**
     * Put pair key-> value to map
     *
     * @param key   to be inserted
     * @param value to be inserted
     * @return value mapped to key
     */
    public V put(K key, V value);

    /**
     * Get value for key
     *
     * @param key to get value for
     * @return value mapped to key
     */
    public V get(K key);

    /**
     * Remove key and value from map
     *
     * @param key to remove from map
     * @return true if removed or false of not found
     */
    public V remove(K key);

    /**
     * Clear map
     */
    public void clear();

    /**
     * Does the map contains the key
     *
     * @param key to locate in map
     * @return true if key is in the map
     */
    public boolean contains(K key);

    /**
     * Number of pairs(key->value) in the map
     *
     * @return number of pairs
     */
    public int size();

    /**
     * Validate the map according to the invariants.
     *
     * @return true if the map is valid
     */
    public boolean validate();

    /**
     * Wrap this map to Java Map
     *
     * @return Java compatible Map
     */

    public java.util.Map<K, V> toMap();
}
