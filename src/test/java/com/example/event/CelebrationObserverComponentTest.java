package com.example.event;

import io.quarkus.arc.Arc;
import io.quarkus.test.InMemoryLogHandler;
import io.quarkus.test.component.QuarkusComponentTestExtension;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Offset;
import org.jboss.logging.Logger;
import org.jboss.logmanager.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CelebrationObserverComponentTest {

    private static final java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("com.example");
    private static final InMemoryLogHandler inMemoryLogHandler = new InMemoryLogHandler(
            record -> record.getLevel().intValue() >= Level.ALL.intValue());

    @BeforeEach
    public void setLogHandler() {
        inMemoryLogHandler.getRecords().clear();
        rootLogger.addHandler(inMemoryLogHandler);
    }

    @AfterEach
    public void afterEach(){
        Arc.container().instance(CelebrationObserver.class).destroy();
    }


    @AfterEach
    public void removeLogHandler() {
        rootLogger.removeHandler(inMemoryLogHandler);
    }

    @RegisterExtension
    static final QuarkusComponentTestExtension componentTestExtension = new QuarkusComponentTestExtension();

    Logger log = Logger.getLogger(CelebrationObserverComponentTest.class);

    @Inject
    Event<CelebrateEvent> event;

    // will not exist if not injected
    @Inject
    CelebrationObserver listener;

    @Test
    void testEventObservers() {
        componentTestExtension.configProperty("shouldSleep", "false");
        componentTestExtension.configProperty("shouldFail", "false");

        long start = System.currentTimeMillis();
        log.infov("firing at {0}", start);
        event.fire(new CelebrateEvent("testing sleep!"));
        long end = System.currentTimeMillis();
        log.infov("finished firing at {0}", end);

        // adding length of sleep
        assertThat(end).isCloseTo(start, Offset.strictOffset(20L));
    }

    @Test
    void testEventObserversDelay() {
        componentTestExtension.configProperty("shouldSleep", "true");
        componentTestExtension.configProperty("shouldFail", "false");

        long start = System.currentTimeMillis();
        log.infov("firing at {0}", start);
        event.fire(new CelebrateEvent("testing sleep!"));
        long end = System.currentTimeMillis();
        log.infov("finished firing at {0}", end);

        // adding length of sleep
        assertThat(end).isNotCloseTo(start, Offset.strictOffset(100L));
    }

    @Test
    void testEventObserversFailure() {
        componentTestExtension.configProperty("shouldSleep", "false");
        componentTestExtension.configProperty("shouldFail", "true");

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
            event.fire(new CelebrateEvent("testing failure!"));
        }).withMessageContaining("shoes");
    }


    @Test
    void testAllObserversAreCalled() {
        componentTestExtension.configProperty("shouldSleep", "false");
        componentTestExtension.configProperty("shouldFail", "false");

        event.fire(new CelebrateEvent("testing all observers"));
        List<LogRecord> logRecords = inMemoryLogHandler.getRecords();

        // cannot test for passed string, as log interpolation isn't resolved in captured log
        //Condition<String> hasEventString = new Condition<>(s -> s.contains("testing all observers"), "has event string");
        Condition<String> hasFirstObserverLog = new Condition<>(s -> s.contains("good times"), "has first observer log");
        Condition<String> hasSecondObserverLog = new Condition<>(s -> s.contains("Failing"), "has second observer log");

        assertThat(logRecords).hasSize(2)
                .extracting(LogRecord::getMessage)
                //.haveExactly(2, hasEventString)
                .haveAtLeastOne(hasFirstObserverLog)
                .haveAtLeastOne(hasSecondObserverLog);

    }
}
