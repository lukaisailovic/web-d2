package model;

public class Table {

    private Player[] players;

    public Table() {
        this.players = new Player[6];
    }

    public synchronized boolean giveSeat(Player player) {
        for (int i = 0; i < 6 ; i++) {
            if(players[i] == null) {
                players[i] = player;
                return true;
            }
        }

        return false;
    }


}
