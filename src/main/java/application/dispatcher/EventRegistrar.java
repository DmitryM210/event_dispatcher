package application.dispatcher;

import application.events.Event;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class EventRegistrar {

    public static void registerAllEvents(HashMap superclasses, HashMap handlers) {
        Reflections reflections = new Reflections("application.events");
        Set<Class<? extends Event>> eventClasses = reflections.getSubTypesOf(Event.class);

        registerEvent(handlers, Event.class);
        var listOfOneEvent = new ArrayList<Class<? extends Event>>();
        listOfOneEvent.add(Event.class);
        registerSuperclasses(superclasses, Event.class, listOfOneEvent);

        for (var eventClass : eventClasses) {
            registerEvent(handlers, eventClass);
            var eventSuperclasses = getAllSuperclasses(eventClass);
            registerSuperclasses(superclasses, eventClass, eventSuperclasses);
        }
    }

    private static <TEvent extends Event> List<Class<? extends Event>> getAllSuperclasses(Class<TEvent> eventClass) {
        List<Class<? extends Event>> superclasses = new ArrayList<>();
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

    private static void registerEvent(HashMap handlers, Class<? extends Event> event) {
        if (!handlers.containsKey(event))
            handlers.put(event, EventHandler.createFor(event));
    }

    private static void registerSuperclasses(HashMap superclasses,
            Class<? extends Event> event, List<Class<? extends Event>> eventSuperclasses) {
        if (!superclasses.containsKey(event))
            superclasses.put(event, eventSuperclasses);
    }

}