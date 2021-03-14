package model;

import java.util.Random;
import java.util.UUID;
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
    private Player winner;
    private int shortStickIndex;
    private final Random random = new Random();
    private final AtomicInteger playedRounds = new AtomicInteger(0);
    private final AtomicInteger playedRoundSteps = new AtomicInteger(0);
    private final int maxRounds;

    public Table(int maxRounds) {
        this.winner = new Player(UUID.randomUUID());
        this.maxRounds = maxRounds;
        this.shortStickIndex = random.nextInt(PLAYERS_IN_GAME);
        this.players = new Player[PLAYERS_IN_GAME];
    }

    public synchronized boolean giveSeat(Player player) {
        if (!gameRunning){
            return false;
        }
        for (int i = 0; i < PLAYERS_IN_GAME ; i++) {
            if(players[i] == null) {
                players[i] = player;
                System.out.println("Igrac "+player.getId()+" ulazi na sto sto");
                if (playersInRound.incrementAndGet() == PLAYERS_IN_GAME){
                    this.gameRunning = true;
                    System.out.println("Igra je pocela sa "+playersInRound.get()+" igraca");
                }
                return true;
            }
        }

        return false;
    }

    public synchronized boolean drawStick(int stickIndex){
        System.out.println("Igrac "+getDrawPlayer().getId()+" vuce stapic " + stickIndex);
        this.drawnStickIndex = stickIndex;
        this.drawPlayerIndex.set(0);
        this.playRoundStep();
        return stickIndex == this.shortStickIndex;
    }
    public void guess(Player player, boolean guess){
        for (int i = 0; i < PLAYERS_IN_GAME; i++){
            if (players[i].getId().equals(player.getId())){
                this.playerGuesses[i] = guess;
                System.out.println("Igrac "+player.getId()+" pogadja " + guess);
            }
        }
        this.playRoundStep();
    }
    public void removePlayerFromTable(Player player){
        for (int i = 0; i < PLAYERS_IN_GAME; i++){
            if (players[i].getId().equals(player.getId())){
                players[i] = null;
            }
        }
        System.out.println("Igrac "+player.getId()+" NAPUSTA sto");
        // reset round params
        this.drawPlayerIndex.set(0);
        this.playedRoundSteps.set(0);
        this.drawnStickIndex = -1;
        this.shortStickIndex = random.nextInt(PLAYERS_IN_GAME);
    }

    private void playRoundStep(){
        int currentSteps = this.playedRoundSteps.incrementAndGet();
        if (currentSteps == 6){
            // next player draw
            int drawIndex = this.drawPlayerIndex.incrementAndGet();
            System.out.println("Sledeci igrac vuce stapic");

            // increase number of rounds
            int currentRounds = this.playedRounds.incrementAndGet();

            System.out.println("Odigrana je runda "+currentRounds+"/"+maxRounds);
            // reset steps
            this.playedRoundSteps.set(0);
            if (drawIndex >5 ){
                System.out.println("Niko od igraca nije izvukao kraci stapic. Igra se zavrsava");
                this.gameRunning = false;
            }
            if (currentRounds >= maxRounds){
                System.out.println("Dostignut je maksimalan broj rundi. Igra se zavrsava");
                this.gameRunning = false;
            }
            if (!this.gameRunning){
                System.out.println("Pobednik je igrac "+winner.getId()+" sa "+ winner.getPoints() +" poena");
            }
        }
    }

    public boolean getResult(Player player){
        if (!gameRunning){
            return false;
        }
        boolean result = false;
        for (int i = 0; i < PLAYERS_IN_GAME; i++){
            if (players[i].getId().equals(player.getId())){
                boolean guess = this.playerGuesses[i];
                boolean shortDraw = this.drawnStickIndex == this.shortStickIndex;
                result = guess == shortDraw;
                if (result){
                    players[i].addPoint();
                    System.out.println("Igrac "+player.getId()+" je dobio poen");
                    if (players[i].getPoints() > winner.getPoints()){
                        this.winner = players[i];
                        System.out.println("Igrac "+player.getId()+" trenutno vodi po broju poena");
                    }
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
