package kytsmen.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class HashMapTest {
    private HashMap<String, Integer> testMap;

    @Test
    public void testMapCreationWithoutSize() {
        testMap = new HashMap<String, Integer>(HashMap.Type.PROBING);
        testMap.put("John Doe", 33);
        testMap.put("Jane Doe", 23);
        assertThat(testMap.get("John Doe"), is(equalTo(33)));
        assertThat(testMap.get("Jane Doe"), is(equalTo(23)));
        assertThat(testMap.size(), is(equalTo(2)));
    }

    @Test
    public void testMapCreationWithSize() {
        testMap = new HashMap<String, Integer>(HashMap.Type.PROBING, 2);
        testMap.put("John Doe", 33);
        testMap.put("Jane Doe", 23);
        assertThat(testMap.get("John Doe"), is(equalTo(33)));
        assertThat(testMap.get("Jane Doe"), is(equalTo(23)));
        assertThat(testMap.size(), is(equalTo(2)));
    }

    @Test
    public void testMapForNull() {
        testMap = new HashMap<String, Integer>(HashMap.Type.PROBING, 2);
        testMap.put("John Doe", 33);
        testMap.put("Jane Doe", 23);
        assertThat(testMap.get("Dmytro Kytsmen"), is(nullValue()));
        assertThat(testMap.get("Jane Doe"), is(equalTo(23)));
    }

    @Test
    public void testMapRemoval() {
        testMap = new HashMap<String, Integer>(HashMap.Type.PROBING, 2);
        testMap.put("John Doe", 33);
        testMap.put("Jane Doe", 23);
        testMap.remove("John Doe");
        assertThat(testMap.get("John Doe"), is(nullValue()));
        assertThat(testMap.get("Jane Doe"), is(equalTo(23)));
        assertThat(testMap.size(), is(equalTo(1)));
        assertThat(testMap.remove("Dmytro Kytsmen"), is(equalTo(null)));
    }

    @Test
    public void testMapForPut() {
        testMap = new HashMap<String, Integer>(HashMap.Type.PROBING, 2);
        assertThat(testMap.put(null, null), is(equalTo(null)));
//        assertThat(testMap.put(null, 42), is(equalTo(42)));

    }

}
