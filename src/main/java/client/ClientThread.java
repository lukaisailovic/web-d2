package client;

import com.google.gson.Gson;
import model.Action;
import model.Request;
import model.Response;
import model.Result;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientThread implements Runnable{

    private static final int PORT = 9999;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Gson gson;

    public ClientThread() throws IOException {
        socket = new Socket("localhost", PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        gson = new Gson();
    }

    public void run() {
        Request request = new Request();
        UUID id = UUID.randomUUID();

        System.out.println("Igrac " + id.toString() + " pokusava da pristupi igri.");

        request.setId(id);
        request.setAction(Action.REQUEST_CHAIR);
        sendRequest(request);

        Response response = receiveResponse();

        if(response.getResult() == Result.SUCCESS) {
            System.out.println("Igrad " + id.toString() + " je uspeo da se prikljuci igri.");
        } else {
            System.out.println("Igrad " + id.toString() + " nije uspeo da se prikljuci igri.");
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
}
