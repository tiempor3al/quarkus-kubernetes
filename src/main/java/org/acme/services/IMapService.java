package org.acme.services;

import jakarta.ws.rs.sse.SseEventSink;

public interface IMapService {
    void put(String key, SseEventSink sseEventSink);
    SseEventSink get(String key);
    void clean();
}
