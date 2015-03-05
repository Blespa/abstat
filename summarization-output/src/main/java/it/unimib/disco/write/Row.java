/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.write;

import java.util.HashMap;
import java.util.Map;

public class Row {

    Map<Entry, String> components;

    public enum Entry {SUBJECT, OBJECT, PREDICATE, SCORE};
    public Row() {
        components = new HashMap<Entry, String>();
    }

    public String get(Entry e) {
        if (components.containsKey(e)) {
            return components.get(e);
        } else {
            return null;
        }
    }

    public void add(Entry key, String s) {
        components.put(key, s);
    }

    public void remove(Entry key) {
        if (components.containsKey(key)) {
            components.remove(key);
        }
    }

    public Row copy() {
        Row f = new Row();
        for (Entry key : components.keySet()) {
            f.add(key, components.get(key));
        }
        return f;
    }
    
    public String toString()
    {
        return components.toString();
    }
}

