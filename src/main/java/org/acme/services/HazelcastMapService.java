package org.acme.services;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.SseEventSink;

@ApplicationScoped
@Priority(100)
public class HazelcastMapService implements IMapService {

    @Inject
    HazelcastInstance hazelcastClient;

    private IMap<String, SseEventSink> map;

    @PostConstruct
    public void init() {
        if (hazelcastClient != null) {
            map = hazelcastClient.getMap("sse-event-sink-map");
        }
    }

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
        map.entrySet().removeIf(entry -> entry.getValue().isClosed());
    }


}
