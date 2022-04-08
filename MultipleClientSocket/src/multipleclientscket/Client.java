package multipleclientscket;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            while (true) {

                String name = scanner.nextLine();
                bufferedWriter.write(name);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                String res = bufferedReader.readLine();
                if ("ok".equals(res)) {
                    username = name;
                    break;
                }
                System.out.println(res);

            }

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);

        }
    }

    public void sendMessage() {
        try {

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    //method for listening to messages from the server
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //executed in separate thread
                String messageFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }

            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Enter your username for group chat: ");

        Socket socket = new Socket("localhost", 12345);
        Client client = new Client(socket);
        client.listenForMessage();
        client.sendMessage();

    }

}
