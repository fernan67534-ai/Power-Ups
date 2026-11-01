package main.com.breakbreak.util;

import main.com.breakbreak.events.CollisionEvent;

public interface CollisionObserver {
    void onCollision(CollisionEvent event);
}
