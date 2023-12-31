package com.example.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CelebrationObserver {

    //@ConfigProperty(name = "shouldFail", defaultValue = "false")
    private boolean shouldFail;

    //@ConfigProperty(name = "shouldSleep", defaultValue = "false")
    private boolean shouldSleep;

    public CelebrationObserver(){}

    // @Inject is supposed to be optional, but properties stay at default if not annotated.
    @Inject
    public CelebrationObserver(@ConfigProperty(name = "shouldFail", defaultValue = "false")
                               boolean shouldFail,
                               @ConfigProperty(name = "shouldSleep", defaultValue = "false")
                               boolean shouldSleep) {
        this.shouldSleep = shouldSleep;
        this.shouldFail = shouldFail;
    }

    private final Logger log = Logger.getLogger(CelebrationObserver.class);

    void celebrate(@Observes CelebrateEvent celebrationEvent) throws InterruptedException {
        if (shouldSleep) {
            log.warnv("sleeping before celebrating {0}", celebrationEvent.getOccasion());
            Thread.sleep(100);
        }
        log.warnv("Celebrate good times! {0}", celebrationEvent.getOccasion());
    }

    void celebrateAndMaybeFail(@Observes CelebrateEvent celebrationEvent) {
        log.warnv("Failing to celebrate! {0}", celebrationEvent.getOccasion());
        if (shouldFail) {
            throw new IllegalStateException("I don't have my celebration shoes on!");
        }
    }
}