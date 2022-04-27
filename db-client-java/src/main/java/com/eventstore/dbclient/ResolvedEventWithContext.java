package com.eventstore.dbclient;

public class ResolvedEventWithContext extends ResolvedEvent {
    private final long firstStreamPosition;
    private final long lastStreamPosition;
    private final Position lastAllStreamPosition;

    ResolvedEventWithContext(long firstStreamPosition, long lastStreamPosition, Position lastAllStreamPosition, RecordedEvent event, RecordedEvent link, Position position) {
        super(event, link, position);
        this.firstStreamPosition = firstStreamPosition;
        this.lastStreamPosition = lastStreamPosition;
        this.lastAllStreamPosition = lastAllStreamPosition;
    }

    public long getFirstStreamPosition() {
        return firstStreamPosition;
    }

    public long getLastStreamPosition() {
        return lastStreamPosition;
    }

    public Position getLastAllStreamPosition() {
        return lastAllStreamPosition;
    }
}
