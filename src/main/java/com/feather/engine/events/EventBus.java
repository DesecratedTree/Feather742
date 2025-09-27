package com.feather.engine.events;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

public class EventBus {
    private final Map<Class<?>, List<EventListener<?>>> listeners = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add((EventListener<Object>) listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        List<EventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener<?> listener : eventListeners) {
                try {
                    ((EventListener<T>) listener).onEvent(event);
                } catch (Exception e) {
                    System.err.println("Error processing event: " + e.getMessage());
                }
            }
        }
    }

    @FunctionalInterface
    public interface EventListener<T> {
        void onEvent(T event);
    }
}