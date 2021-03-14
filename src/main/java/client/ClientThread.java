package client;

import com.google.gson.Gson;
import model.Action;
import model.Request;
import model.Response;
import model.Result;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

public class ClientThread implements Runnable {

    private static final int PORT = 9999;

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Random random = new Random();

    private final Gson gson;

    public ClientThread() throws IOException {
        socket = new Socket("localhost", PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        gson = new Gson();
    }

    public void run() {
        try {
            Request request = new Request();
            UUID id = UUID.randomUUID();

            System.out.println("Igrac " + id.toString() + " pokusava da pristupi igri.");

            request.setId(id);
            request.setAction(Action.REQUEST_CHAIR);
            // 1
            sendRequest(request);

            // 2
            Response response = receiveResponse();

            if (response.getResult() == Result.SUCCESS) {
                System.out.println("Igrac " + id.toString() + " je uspeo da se prikljuci igri.");
            } else {
                System.out.println("Igrac " + id.toString() + " nije uspeo da se prikljuci igri.");
                return;
            }
            // 3
            response = receiveResponse();
            while(response.getResult() != Result.GAME_END){
                request = new Request();
                request.setId(id);
                if (response.getResult().equals(Result.DRAW_STICK)){
                    request.setAction(Action.DRAW_STICK);
                    request.setData(String.valueOf(random.nextInt(6)));
                } else {
                    request.setAction(Action.GUESS);
                    request.setData(String.valueOf(random.nextBoolean()));
                }
                // 4
                sendRequest(request);

                // 5
                response = receiveResponse();
                System.out.println(response.getData() == null);

                // 3
                response = receiveResponse();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

    }

    public void sendRequest(Request request) {
        out.println(gson.toJson(request));
    }

    public Response receiveResponse() {
        try {
            return gson.fromJson(in.readLine(), Response.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void closeConnection(){
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
