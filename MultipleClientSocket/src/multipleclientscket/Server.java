package multipleclientscket;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            ClientHandler.sendMessage();
            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                System.out.println("A new Client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeServerSocket(){
        try{
            if(serverSocket != null)
            {
                serverSocket.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException{
        
        ServerSocket serverSocket = new ServerSocket(12345);
        Server server = new Server(serverSocket);
        server.startServer();
    }

}
