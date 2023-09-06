package com.example.event;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LifecycleCallbacks {

    private final Logger log = Logger.getLogger(LifecycleCallbacks.class);

    // if recorded in a non-static member, sequence is empty on retrieval in the test
    public static final List<String> callbackSequence = new ArrayList<>();

    @PostConstruct
    void postConstruct(){
        log.info("LifecycleCallbacks - postContruct invoked");
        callbackSequence.add("postContruct");
    }

    public void onInitialized(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
        log.info("LifecycleCallbacks - onInitialized invoked");
        callbackSequence.add("onInitialized");
    }

    void onStartupEvent(@Observes StartupEvent ev) {
        log.info("LifecycleCallbacks - onStartupEvent invoked");
        callbackSequence.add("onStartupEvent");
    }

    void onShutdownEvent(@Observes ShutdownEvent ev){
        log.info("LifecycleCallbacks - onShutdownEvent invoked");
        callbackSequence.add("onShutdownEvent");
    }

    @PreDestroy
    void preDestroy(){
        log.info("LifecycleCallbacks - preDestroy invoked");
        callbackSequence.add("preDestroy");
    }

    public void onDestroyed(@Observes @Destroyed(ApplicationScoped.class) Object pointless) {
        log.info("LifecycleCallbacks - onDestroyed invoked");
        callbackSequence.add("onDestroyed");
    }
}
