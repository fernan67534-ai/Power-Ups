package main.com.breakbreak.events;

import main.com.breakbreak.enums.CollisionType;

public record CollisionEvent(
        Object first,
        Object second,
        CollisionType type
) {}
