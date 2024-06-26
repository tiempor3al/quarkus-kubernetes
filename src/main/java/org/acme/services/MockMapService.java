package org.acme.services;


import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ApplicationScoped
@DefaultBean
public class MockMapService implements IMapService {

    private final Map<String, SseEventSink> map = new ConcurrentHashMap<>();

    @Override
    public void put(String key, SseEventSink sseEventSink) {
        map.put(key, sseEventSink);
    }

    @Override
    public SseEventSink get(String key) {
        return map.get(key);
    }

    @Override
    public void clean() {
        if (!map.isEmpty()) {
            map.entrySet().removeIf(entry -> entry.getValue().isClosed());
        }
    }


}
