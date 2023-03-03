package Assignment3Starter;

import org.json.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static  void main(String args[]) throws IOException{

        Scanner scanner = new Scanner(System.in);

        try{
            if(args.length != 2){
                System.out.println("Run like so: gradle Client (optional: '-Phost=' and '-Pport=')");
                System.exit(0);
            }

            int port = -1;

            try{
                port = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e){
                System.out.println("Port must be a integer number!");
                System.exit(2);
            }

            String host = args[0];
            Socket sock = new Socket(host, port);
            System.out.println("Connected to server with IP: " + host + " at port: " + port);
            InputStream in = sock.getInputStream();
            OutputStream out = sock.getOutputStream();
            ObjectOutputStream oStream = new ObjectOutputStream(out);
            ObjectInputStream iStream = new ObjectInputStream(in);
            oStream.writeObject("{'type':'game'}");
            oStream.flush();

            loop: while(true){
                String servResponse = (String) iStream.readObject();
                JSONObject serverResponse = new JSONObject(servResponse);

                if(serverResponse.getBoolean("ok")){
                    if(serverResponse.getString("type").equals("game")){
                        System.out.println("Gui Opening");
                    }
                    else if(serverResponse.getString("type").equals("leader")){
                        System.out.println("Printing Leaderboard");
                    }
                    else if(serverResponse.getString("type").equals("quit")){
                        System.out.println("Quit");
                        oStream.writeObject("{'type':'quit'}");
                        break loop;
                    }
                    else if(serverResponse.getString("type").equals("name")){
                        System.out.println("Getting Name");
                    }
                    else if(serverResponse.getString("type").equals("start")){
                        System.out.println("Starting Game");
                    }
                    else if(serverResponse.getString("type").equals("more")){
                        System.out.println("Getting New Quote");
                    }
                    else if(serverResponse.getString("type").equals("eval")){
                        System.out.println("Getting Evaluation");
                    }
                }
            }
            scanner.close();
            oStream.close();
            iStream.close();
            sock.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
