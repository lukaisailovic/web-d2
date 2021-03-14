package model;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Table {
    private static final int PLAYERS_IN_GAME = 6;
    private final Player[] players;
    private boolean gameRunning = true;
    private final AtomicInteger playersInRound = new AtomicInteger(0);
    private final AtomicInteger drawPlayerIndex = new AtomicInteger(0);
    private final CyclicBarrier barrier = new CyclicBarrier(6);
    private int shortStickIndex;
    private final Random random = new Random();

    public Table() {
        this.shortStickIndex = random.nextInt(PLAYERS_IN_GAME);
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

    public synchronized boolean drawStick(int stickIndex){
        System.out.println("Igrac "+getDrawPlayer().getId()+" vuce stapic " + stickIndex);
        return stickIndex == this.shortStickIndex;
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
