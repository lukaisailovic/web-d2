package server;

import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.Socket;

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

            while(table.isGameRunning()){

            }




        } catch (IOException e) {
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
