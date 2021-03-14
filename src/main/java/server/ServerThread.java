package server;

import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;

public class ServerThread extends Thread {

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    private Gson gson;
    private Table table;

    public ServerThread(Socket socket, Table table) {
        this.socket = socket;
        this.table = table;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gson = new Gson();
    }

    public void run() {

        try {
            // 1
            Request request = receiveRequest();

            Player player = new Player(request.getId());

            Response response = new Response();

            if(request.getAction() == Action.REQUEST_CHAIR) {
                if(!table.giveSeat(player)){
                    response.setResult(Result.FAILURE);
                    // 2
                    sendResponse(response);
                    return;
                } else {
                    response.setResult(Result.SUCCESS);
                    // 2
                    sendResponse(response);
                }
            }

             // wait until game starts
            table.getBarrier().await();

            while(table.isGameRunning()){

                // if player leaves, wait until there is new player
                table.getBarrier().await();

                Player drawPlayer = table.getDrawPlayer();
                response = new Response();
                if (drawPlayer.getId().equals(player.getId())){
                    response.setResult(Result.DRAW_STICK);
                } else {
                    response.setResult(Result.GUESS);
                }
                // 3
                sendResponse(response);
                System.out.println("Sending response from server "+ response.getResult());

                // 4 player guess
                request = receiveRequest();


                response = new Response();
                if (request.getAction().equals(Action.GUESS)){
                   table.guess(player,Boolean.parseBoolean(request.getData()));
                }
                // wait for all players to guess before draw
                table.getBarrier().await();
                if (request.getAction().equals(Action.DRAW_STICK)){
                    if (table.drawStick(Integer.parseInt(request.getData()))){
                        response.setResult(Result.DRAW_SHORT);
                        response.setData("Ivucen je kratak stapic");
                    } else {
                        response.setResult(Result.DRAW_LONG);
                        response.setData("Ivucen je dugacak stapic");
                    }

                } else {
                    response.setResult(Result.GUESS_CORRECT);
                    response.setData("Tacno");
                }
                // TODO after draw is complete, wait for all players and register points

                // 5
                // TODO send player correct/incorrect/leave table


                sendResponse(response);
            }




        } catch (IOException | BrokenBarrierException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Request receiveRequest() throws IOException {
        return gson.fromJson(in.readLine(), Request.class);
    }

    private void sendResponse(Response response) {
        out.println(gson.toJson(response));
    }
}
