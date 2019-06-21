package com.yanwu.spring.cloud.common.core.logging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class EventData implements Serializable {

    private static final long serialVersionUID = -1672684219438986148L;

    // use linked hashmap to maintain order of hash entries
    public enum EventDataType {
        EventMessage, EventType, EventID,
    }

    private HashMap<String, String> data = new HashMap<String, String>();

    public void add(EventDataType type, String value) {
        if (type != null && value != null) {
            data.put(type.name(), value);
        }
    }

    public void get(EventDataType type) {
        data.get(type.name());
    }

    public void clear() {
        data.clear();
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        Iterator<Entry<String, String>> it = data.entrySet().iterator();
        boolean first = true;
        while (it.hasNext()) {
            Entry<String, String> one = it.next();
            if (first) {
                buf.append(one.getKey()).append("=").append(one.getValue());
                first = false;
            } else {
                buf.append("&").append(one.getKey()).append("=").append(one.getValue());
            }
        }
        return buf.toString();
    }

    @Override
    public int hashCode() {
        return this.data.hashCode();
    }

}