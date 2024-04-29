package org.acme.services;



import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ApplicationScoped
@Alternative
@Priority(1)
public class MockMapService implements IMapService {

    private final Map<String, SseEventSink> map =new ConcurrentHashMap<>();

    @Override
    public void put(String key, SseEventSink sseEventSink) {
        map.put(key, sseEventSink);
    }

    @Override
    public SseEventSink get(String key){
        return map.get(key);
    }

    @Override
    public void clean(){
        map.entrySet().removeIf(entry -> entry.getValue().isClosed());
    }


}
