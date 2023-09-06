package com.example.event;

import io.quarkus.arc.Arc;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class LifecycleCallbacksTest {

    // no need to inject explicitly, if not component test
    //@Inject
    //LifecycleCallbacks lcb;

    // without the explicit destruction, the "preDestroy" event is not captured
    // with QuarkusComponentTest, there is no StartupEvent
    // not reaching the onDestroy event in test
    // onInitialized should not be used:
    // The method com.example.event.LifecycleCallbacks#onInitialized is an observer for
    // @Initialized(ApplicationScoped.class). Observer notification for this event may vary
    // between JVM and native modes! We strongly recommend to observe StartupEvent instead
    // as that one is consistently delivered in both modes once the container is running.
    @Test
    public void testHelloEndpoint() {
        Arc.container().instance(LifecycleCallbacks.class).destroy();
        var expected = List.of("postContruct", "onInitialized", "onStartupEvent", "preDestroy");
        assertThat(LifecycleCallbacks.callbackSequence).isEqualTo(expected);
    }
}