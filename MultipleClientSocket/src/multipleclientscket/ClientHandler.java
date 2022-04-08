package multipleclientscket;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    //broadcast messages to all clients instead of just the server.
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    ClientHandler(){
        
    }
    
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //System.out.println("test");
            while (true) {
                boolean veri = true;
                String name = bufferedReader.readLine();
                //System.out.println(name);
                for (ClientHandler c : clientHandlers) {
                    if (c.clientUsername.equals(name)) {
                        veri = false;
                        break;
                    }
                }
                if (veri) {
                    this.clientUsername = name;
                    this.bufferedWriter.write("ok");
                    this.bufferedWriter.newLine();
                    this.bufferedWriter.flush();
                    break;

                }
                this.bufferedWriter.write("This username already exist. Plase enter different name");
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
            }

            clientHandlers.add(this);

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        //everything in this method is run on a separate thread
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                System.out.println(messageFromClient);

                //broadcastMessage();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }

        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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

    public static void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Scanner input = new Scanner(System.in);
                while (true) {
                    String s = input.nextLine();

                    if (s.equalsIgnoreCase("all")) {
                        String messageToSend = input.nextLine();
                        for (ClientHandler clientHandler : clientHandlers) {
                            try {

                                clientHandler.bufferedWriter.write("Server : " + messageToSend);
                                clientHandler.bufferedWriter.newLine();
                                clientHandler.bufferedWriter.flush();

                            } catch (IOException e) {
                                System.out.println("IOException : "+e);
                                //closeEverything(socket, bufferedReader, bufferedWriter);
                            }
                        }
                    } else {
                        //String messageToSend = input.nextLine();
                        boolean ok = false;
                        for (ClientHandler clientHandler : clientHandlers) {
                            try {
                                if (clientHandler.clientUsername.equals(s)) {
                                    String messageToSend = input.nextLine();
                                    clientHandler.bufferedWriter.write("Server : " + messageToSend);

                                    clientHandler.bufferedWriter.newLine();
                                    clientHandler.bufferedWriter.flush();
                                    ok = true;
                                    break;
                                }
                               
                                
                            } catch (IOException e) {
                                System.out.println("IOException : "+e);
                                //closeEverything(socket, bufferedReader, bufferedWriter);
                            }
                        }
                         if (!ok) {
                                    System.out.println("No client is connected with that name");
                         }

                    }

                }
            }
        }).start();
    }
}
