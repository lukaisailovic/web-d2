package model;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Table {
    private static final int PLAYERS_IN_GAME = 6;
    private final Player[] players;
    private boolean gameRunning = false;
    private final AtomicInteger playersInRound = new AtomicInteger(0);
    private final AtomicInteger drawPlayerIndex = new AtomicInteger(0);
    private final CyclicBarrier barrier = new CyclicBarrier(6);

    public Table() {
        this.players = new Player[PLAYERS_IN_GAME];
    }

    public synchronized boolean giveSeat(Player player) {
        for (int i = 0; i < PLAYERS_IN_GAME ; i++) {
            if(players[i] == null) {
                players[i] = player;
                if (playersInRound.incrementAndGet() == PLAYERS_IN_GAME){
                    this.gameRunning = true;
                    System.out.println("Game started with "+playersInRound.get()+" players");
                }
                return true;
            }
        }

        return false;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public AtomicInteger getPlayersInRound() {
        return playersInRound;
    }

    public CyclicBarrier getBarrier() {
        return barrier;
    }
    public Player getDrawPlayer(){
        return this.players[this.drawPlayerIndex.get()];
    }
}
