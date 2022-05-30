package domain.dispatcher;

import domain.Event;
import domain.EventHandler;
import org.reflections.Reflections;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DispatcherBuilder {

    private final Set<Class<? extends Event>> events;
    private final HashMap<Class<? extends Event>, Set<Class<? extends Event>>> superclasses;
    private final HashMap<Class<? extends Event>, EventHandler<? extends Event>> handlers;
    private final HashMap<Class<? extends Event>, EventHandler<? extends Event>> assembledHandlers;

    public DispatcherBuilder() {
        events = new HashSet<>();
        superclasses = new HashMap<>();
        handlers = new HashMap<>();
        assembledHandlers = new HashMap<>();
    }

    public DispatcherBuilder registerAllEvents() {
        Reflections reflections = new Reflections("application.events");
        Set<Class<? extends Event>> eventClasses = reflections.getSubTypesOf(Event.class);

        registerEvent(Event.class);
        var listOfOneEvent = new HashSet<Class<? extends Event>>();
        listOfOneEvent.add(Event.class);
        registerSuperclasses(Event.class, listOfOneEvent);

        for (var eventClass : eventClasses) {
            registerEvent(eventClass);
            var eventSuperclasses = getAllSuperclasses(eventClass);
            registerSuperclasses(eventClass, eventSuperclasses);
        }

        return this;
    }

    public <TEvent extends Event> DispatcherBuilder withHandler(Class<TEvent> event, Consumer<TEvent> action) {
        EventHandler<TEvent> newHandler = new EventHandler<TEvent>(action);
        EventHandler<TEvent> foundHandler = (EventHandler<TEvent>)handlers.get(event);
        if (foundHandler != null)
            handlers.put(event, foundHandler.union(newHandler));
        return this;
    }

    public Dispatcher build() {
        finalizeRegistration();
        return new Dispatcher(assembledHandlers);
    }

    private <TEvent extends Event> Set<Class<? extends Event>> getAllSuperclasses(Class<TEvent> eventClass) {
        Set<Class<? extends Event>> superclasses = new HashSet<>();
        Class<? super TEvent> superclass = eventClass;
        while (superclass != null) {
            var interfaces = Arrays.stream(superclass.getInterfaces())
                .map(i -> (Class<? extends Event>)i).collect(Collectors.toList());
            superclasses.addAll(interfaces);
            if (superclass == Object.class)
                break;
            superclasses.add((Class<? extends Event>)superclass);
            superclass = superclass.getSuperclass();
        }
        return superclasses;
    }

    private void registerEvent(Class<? extends Event> event) {
        events.add(event);
        if (!handlers.containsKey(event))
            handlers.put(event, EventHandler.createEmptyFor(event));
    }

    private void registerSuperclasses(Class<? extends Event> event, Set<Class<? extends Event>> eventSuperclasses) {
        if (!superclasses.containsKey(event))
            superclasses.put(event, eventSuperclasses);
    }

    private <TEvent extends Event> void finalizeRegistration() {
        for (var event : events) {
            var eventSuperclasses = superclasses.get(event);
            EventHandler<TEvent> assembledHandler = (EventHandler<TEvent>)EventHandler.createEmptyFor(event);
            for (var superclass : eventSuperclasses) {
                EventHandler<TEvent> handler = (EventHandler<TEvent>)handlers.get(superclass);
                assembledHandler = assembledHandler.union(handler);
            }
            assembledHandlers.put(event, assembledHandler);
        }
    }

}