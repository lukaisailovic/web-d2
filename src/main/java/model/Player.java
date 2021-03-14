package model;

import java.util.UUID;

public class Player {

    private UUID id;
    private int points = 0;

    public Player(UUID id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void addPoint() {
        points++;
    }

    public UUID getId() {
        return id;
    }
}
