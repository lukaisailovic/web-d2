package model;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Table {
    private static final int PLAYERS_IN_GAME = 6;
    private final Player[] players;
    private final boolean[] playerGuesses = new boolean[PLAYERS_IN_GAME];
    private boolean gameRunning = true;
    private final AtomicInteger playersInRound = new AtomicInteger(0);
    private final AtomicInteger drawPlayerIndex = new AtomicInteger(0);
    private final CyclicBarrier barrier = new CyclicBarrier(6);
    private int drawnStickIndex = -1;
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
        this.drawnStickIndex = stickIndex;
        return stickIndex == this.shortStickIndex;
    }
    public void guess(Player player, boolean guess){
        for (int i = 0; i < PLAYERS_IN_GAME; i++){
            if (players[i].getId().equals(player.getId())){
                this.playerGuesses[i] = guess;
                System.out.println("Igrac "+player.getId()+" pogadja " + guess);
            }
        }
    }
    public boolean getResult(Player player){
        boolean result = false;
        for (int i = 0; i < PLAYERS_IN_GAME; i++){
            if (players[i].getId().equals(player.getId())){
                boolean guess = this.playerGuesses[i];
                boolean shortDraw = this.drawnStickIndex == this.shortStickIndex;
                result = guess == shortDraw;
                if (result){
                    players[i].addPoint();
                    System.out.println("Igrac "+player.getId()+" je dobio poen");
                }
                break;
            }
        }
        return result;
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
