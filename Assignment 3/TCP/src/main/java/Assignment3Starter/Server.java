package Assignment3Starter;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import org.json.*;
public class Server {

    public static void main(String args[]){

        ClientGui cGui = new ClientGui();
        Socket cSock = null;
        Boolean connected = false;
        ObjectInputStream iStream = null;
        OutputStream output = null;
        ObjectOutputStream oStream = null;
        boolean updated = false;
        Integer updateCode = -1;

        try{
            if(args.length != 1){
                System.out.println("Run like so: gradle Server (optional: -Pport= )");
                System.exit(0);
            }
            int port = -1;
            try {
                port = Integer.parseInt(args[0]);
            }

            catch (NumberFormatException e){
                System.out.println("Pport must be a integer number!");
                System.exit(2);
            }

            ServerSocket servSock = new ServerSocket(port);
            System.out.println("Ready for connection");
            Map<String, Integer> leaderboard = new HashMap<String, Integer>();

            while(true){
                if(!connected){
                    System.out.println("Waiting for connection");
                    cSock = servSock.accept();
                    iStream = new ObjectInputStream(cSock.getInputStream());
                    output = cSock.getOutputStream();
                    oStream = new ObjectOutputStream(output);
                    connected = true;
                }

                if(iStream != null){
                    String requestJSON = (String) iStream.readObject();
                    System.out.println(requestJSON);
                    JSONObject request = new JSONObject(requestJSON);
                    System.out.println("Server received type: " + request.getString("type"));

                    if(request.getString("type").equals("game")){
                        cGui = new ClientGui();
                        cGui.newGame(1);
                        cGui.insertImage("img/hi.png", 0, 0);
                        cGui.outputPanel.appendOutput("Please enter your name");
                        cGui.show(false);

                        JSONObject response = new JSONObject();
                        response.put("ok", true);
                        response.put("type", "game");

                        oStream.writeObject(response.toString());
                        oStream.flush();
                    }

                    else if(request.getString("type").equals("quit")){
                        oStream.close();
                        iStream.close();
                        cSock.close();
                        connected = false;
                    }
                }

                boolean gameOn = connected;

                while(gameOn){
                    updated = cGui.updated;
                    if(updated){
                        updateCode = cGui.updateCode;
                        JSONObject response = new JSONObject();

                        switch (updateCode){
                            case 1:
                                response.put("ok", true);
                                response.put("type", "name");
                                break;

                            case 2:
                                response.put("ok", true);
                                response.put("type", "leader");
                                break;

                            case 3:
                                response.put("ok", true);
                                response.put("type", "start");
                                break;

                            case 4:
                                response.put("ok", true);
                                response.put("type", "more");
                                break;

                            case 5:
                                response.put("ok", true);
                                response.put("type", "eval");
                                for(int i = 0; i < cGui.leaderboard.size(); i++){
                                    leaderboard.putAll(cGui.leaderboard);
                                }
                                break;

                            case 0:
                                response.put("ok", true);
                                response.put("type", "quit");
                                gameOn = false;
                                break;

                            default:
                                response.put("ok", false);
                                break;
                        }

                        oStream.writeObject(response.toString());
                        oStream.flush();

                        cGui.updated = false;
                        cGui.updateCode = -1;
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
