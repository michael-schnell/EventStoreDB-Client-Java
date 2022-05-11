package com.eventstore.dbclient;

import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testcontainers.module.ESDBTests;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ReadStreamReactiveTests extends ESDBTests {
    @Test
    public void testReadStreamReactiveForward10EventsFromPositionStart() throws Throwable {
        EventStoreDBClient client = getPopulatedServer().getClient();

        ReadStreamOptions options = ReadStreamOptions.get()
                .forwards()
                .fromStart()
                .notResolveLinkTos();

        List<ResolvedEventWithContext> events = Flowable.fromPublisher(client.readStreamReactive("dataset20M-1800", 10, options))
                .collect(toList())
                .blockingGet();

        verifyAgainstTestData(events, "dataset20M-1800-e0-e10");
    }

    @Test
    public void testReadStreamReactiveBackward10EventsFromPositionEnd() throws Throwable {
        EventStoreDBClient client = getPopulatedServer().getClient();

        ReadStreamOptions options = ReadStreamOptions.get()
                .backwards()
                .fromEnd()
                .notResolveLinkTos();

        List<ResolvedEventWithContext> events = Flowable.fromPublisher(client.readStreamReactive("dataset20M-1800", 10, options))
                .collect(toList())
                .blockingGet();

        verifyAgainstTestData(events, "dataset20M-1800-e1999-e1990");
    }

    @Test
    public void testReadStreamOptionsAreNotIgnoredInOverloadedMethod() throws Throwable {
        EventStoreDBClient client = getPopulatedServer().getClient();

        ReadStreamOptions options = ReadStreamOptions.get()
                .backwards()
                .fromEnd()
                .notResolveLinkTos();

        List<ResolvedEventWithContext> events1 = Flowable.fromPublisher(client.readStreamReactive("dataset20M-1800", Long.MAX_VALUE, options))
                .collect(toList())
                .blockingGet();

        List<ResolvedEventWithContext> events2 = Flowable.fromPublisher(client.readStreamReactive("dataset20M-1800", options))
                .collect(toList())
                .blockingGet();

        RecordedEvent firstEvent1 = events1.get(0).getOriginalEvent();
        RecordedEvent firstEvent2 = events2.get(0).getOriginalEvent();

        Assertions.assertEquals(events1.size(), events2.size());
        Assertions.assertEquals(firstEvent1.getEventId(), firstEvent2.getEventId());
    }

    private void verifyAgainstTestData(List<ResolvedEventWithContext> actualEvents, String filenameStem) {
        ResolvedEvent[] actualEventsArray = actualEvents.toArray(new ResolvedEvent[0]);

        TestResolvedEvent[] expectedEvents = TestDataLoader.loadSerializedResolvedEvents(filenameStem);
        for (int i = 0; i < expectedEvents.length; i++) {
            TestResolvedEvent expected = expectedEvents[i];
            ResolvedEvent actual = actualEventsArray[i];

            expected.assertEquals(actual);
        }
    }
}
