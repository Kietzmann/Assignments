package kytsmen.util;

import kytsmen.util.interfaces.IMap;

import java.util.*;

@SuppressWarnings("unchecked")
public class HashMap<K, V> implements IMap<K, V> {

    private HashMap<K, V> delegateMap;

    public HashMap(Type type, int size) {
        if (type == Type.PROBING) {
            delegateMap = new ProbingHashMap<K, V>(size);
        }
    }

    public HashMap(Type type) {
        if (type == Type.PROBING) {
            delegateMap = new ProbingHashMap<K, V>();
        }
    }

    private HashMap() {

    }

    public V put(K key, V value) {
        return delegateMap.put(key, value);
    }

    public V get(K key) {
        return delegateMap.get(key);
    }

    public V remove(K key) {
        return delegateMap.remove(key);
    }

    public void clear() {
        delegateMap.clear();
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public int size() {
        return delegateMap.size();
    }

    public boolean validate() {
        return delegateMap.validate();
    }

    public Map<K, V> toMap() {
        return delegateMap.toMap();
    }

    public String toString() {
        return delegateMap.toString();
    }

    public enum Type {PROBING}

    private static class ProbingHashMap<K, V> extends HashMap<K, V> {
        private int hashingKey = -1;
        private float loadFactor = 0.6f;
        private int minimumSize = 1024;
        private Pair<K, V>[] array;
        private int size = 0;

        public ProbingHashMap(int size) {
            initializeMap(size);
        }

        public ProbingHashMap() {
            initializeMap(minimumSize);
        }

        private static final int getLargerSize(int input) {
            return input << 1;
        }

        private static final int getSmallerSize(int input) {
            return input >> 1 >> 1;
        }

        private int getNextIndex(int input) {
            int i = input + 1;
            if (i >= array.length) {
                i = 0;
            }
            return i;
        }

        @Override
        public V remove(K key) {
            int index = indexOf(key);
            Pair<K, V> prev = null;

            Pair<K, V> pair = array[index];
            if (pair != null && pair.key.equals(key)) {
                prev = array[index];
                array[index] = null;
                --size;

                int loadFactored = (int) (size / loadFactor);
                int smallerSize = getSmallerSize(array.length);
                if (loadFactored < smallerSize && smallerSize > minimumSize) {
                    reduce();
                }
                return prev.value;
            }

            int start = getNextIndex(index);
            while (start != index) {
                pair = array[start];
                if (pair != null && pair.key.equals(key)) {
                    array[start] = null;
                    --size;

                    int loadFactored = (int) (size / loadFactor);
                    int smallerSize = getSmallerSize(array.length);
                    if (loadFactored < smallerSize && smallerSize > minimumSize) {
                        reduce();
                    }
                    return prev.value;
                }
                start = getNextIndex(start);
            }
            return null;
        }

        @Override
        public V get(K key) {
            int index = indexOf(key);
            Pair<K, V> pair = array[index];
            if (pair == null) {
                return null;
            }
            if (pair.key.equals(key)) {
                return pair.value;
            }
            int start = getNextIndex(index);
            while (start != index) {
                pair = array[start];
                if (pair == null) {
                    return null;
                }
                if (pair.key.equals(key)) {
                    return pair.value;
                }
                start = getNextIndex(start);
            }
            return null;
        }

        @Override
        public boolean contains(K key) {
            return get(key) != null;
        }

        @Override
        public void clear() {
            for (int i = 0; i < array.length; ++i) {
                array[i] = null;
            }
            size = 0;
        }

        @Override
        public int size() {
            return size;
        }

        private void initializeMap(int current) {
            int length = getLargerSize(current);
            array = new Pair[length];
            size = 0;
            hashingKey = length;
        }

        private void increase() {
            Pair<K, V>[] temp = array;

            int length = getLargerSize(array.length);

            initializeMap(length);

            for (Pair<K, V> pair : temp) {
                if (pair != null) {
                    put(pair);
                }
            }
        }

        private void reduce() {
            Pair<K, V>[] temp = array;

            int length = getSmallerSize(array.length);

            initializeMap(length);

            for (Pair<K, V> pair : temp) {
                if (pair != null) {
                    put(pair);
                }
            }
        }

        @Override
        public V put(K key, V value) {
            return put(new Pair<K, V>(key, value));
        }

        private int hash(K key) {
            return key == null ? 0 : key.hashCode();
        }

        private V put(Pair<K, V> newPair) {
            V prev = null;
            int index = indexOf(newPair.key);

            Pair<K, V> pair = array[index];

            if (pair == null) {
                array[index] = newPair;
                size++;

                int maxSize = (int) (loadFactor * array.length);
                if (size >= maxSize) {
                    increase();
                }
                return prev;
            }

            if (pair.key.equals(newPair.key)) {
                prev = pair.value;
                pair.value = newPair.value;
                return prev;
            }

            int start = getNextIndex(index);
            while (start != index) {
                pair = array[start];
                if (pair == null) {
                    array[start] = newPair;
                    ++size;

                    int maxSize = (int) (loadFactor * array.length);
                    if (size >= maxSize) {
                        increase();
                    }
                    return prev;
                }

                if (pair.key.equals(newPair.key)) {
                    prev = pair.value;
                    pair.value = newPair.value;
                    return prev;
                }

                start = getNextIndex(start);
            }

            return null;
        }

        private int indexOf(K key) {
            int k = Math.abs(hash(key)) % hashingKey;
            if (k >= array.length) {
                k -= k / array.length * array.length;
            }
            return k;
        }


        @Override
        public Map<K, V> toMap() {
            return new JavaCompatibleHashMap<K, V>(this);
        }

        @Override
        public boolean validate() {
            Set<K> keys = new HashSet<K>();
            for (Pair<K, V> pair : array) {
                if (pair == null) {
                    continue;
                }

                K k = pair.key;
                V v = pair.value;

                if (k == null || v == null) {
                    return false;
                }
                if (keys.contains(k)) {
                    return false;
                }
                keys.add(k);
            }
            return keys.size() == size();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int key = 0; key < array.length; ++key) {
                Pair<K, V> p = array[key];
                if (p == null) {
                    continue;
                }

                V value = p.value;
                if (value != null) {
                    builder.append(key).append("=").append(value).append(", ");
                }
            }
            return builder.toString();
        }


        private static class JavaCompatibleHashMap<K, V> extends AbstractMap<K, V> {
            private ProbingHashMap<K, V> map;

            protected JavaCompatibleHashMap(ProbingHashMap<K, V> map) {
                this.map = map;
            }

            @Override
            public V put(K key, V value) {
                return map.put(key, value);
            }

            @Override
            public V remove(Object key) {
                return map.remove((K) key);
            }

            @Override
            public boolean containsKey(Object key) {
                return map.contains((K) key);
            }

            @Override
            public void clear() {
                map.clear();
            }

            @Override
            public int size() {
                return map.size();
            }

            @Override
            public Set<Map.Entry<K, V>> entrySet() {
                Set<Map.Entry<K, V>> set = new HashSet<Entry<K, V>>() {
                    private static final long serialVersionUID = 1L;

                    public Iterator<Map.Entry<K, V>> iterator() {
                        return (new JavaCompatibleIteratorWrapper<K, V>(map, super.iterator()));
                    }
                };
                for (Pair<K, V> p : map.array) {
                    if (p == null) {
                        continue;
                    }

                    Map.Entry<K, V> entry = new JavaCompatibleMapEntry<K, V>(p.key, p.value);
                    set.add(entry);
                }
                return set;
            }
        }


        private static class JavaCompatibleMapEntry<K, V> extends AbstractMap.SimpleEntry<K, V> {
            private static final long serialVersionUID = 231254211553123L;

            public JavaCompatibleMapEntry(K key, V value) {
                super(key, value);
            }
        }


    }

    private static final class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return 31 * (key.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (!(obj instanceof Pair)) {
                return false;
            }

            Pair<K, V> pair = (Pair<K, V>) obj;
            return key.equals(pair.key);
        }
    }

    private static class JavaCompatibleIteratorWrapper<K, V> implements Iterator<Map.Entry<K, V>> {
        private HashMap<K, V> map;
        private Iterator<Map.Entry<K, V>> iterator;
        private Map.Entry<K, V> lastEntry;

        public JavaCompatibleIteratorWrapper(HashMap<K, V> map, Iterator<Map.Entry<K, V>> iterator) {
            this.map = map;
            this.iterator = iterator;
        }

        public boolean hasNext() {
            if (iterator == null) {
                return false;
            }
            return iterator.hasNext();
        }

        public Map.Entry<K, V> next() {
            if (iterator == null) {
                return null;
            }
            lastEntry = iterator.next();
            return lastEntry;
        }

        public void remove() {
            if (iterator == null || lastEntry == null) {
                return;
            }
            map.remove(lastEntry.getKey());
            iterator.remove();
        }
    }


}
