import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.Serializable;

public class LeaderNode implements Serializable{
    private static int port;
    private final Map<Integer, Client> clientConnects; //maybe not needed
    //private final Map<Bank,Socket> bankConnects; // list of banks...change to client connect
    private List<BankConnection> bankConnects; // this will be list of bank connections. create bankConnection class + constructor
    //constructor type(is it a bank or client?), socket, input and output streams
    private Socket conn;
    private ExecutorService executorService;


    public LeaderNode(int port) {
        this.port = port;
        this.clientConnects = new HashMap<>();
        this.bankConnects = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(10); // Create a thread pool with 10 threads

    }

    public void run() throws ClassNotFoundException {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Leader node started on port " + port);
            while (true) {
                System.out.println("Waiting for connection on port: " + port);
                Socket socketIn = serverSocket.accept();
                System.out.println("received connection");
                ObjectOutputStream out = new ObjectOutputStream(socketIn.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socketIn.getInputStream());
                int type = (int) in.readObject();
                if(type == 0){ // if its a bank node
                    System.out.println("Recieived connection of type: Bank");
                    executorService.submit(() -> {
                        try {
                            Bank bank = (Bank) in.readObject();
                            int id = bank.id;
                            BankConnection bConnect = new BankConnection(bank,socketIn,in,out);
                            bankConnects.add(bConnect);
                            handleBankConnection(bConnect);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                else if(type == 1){ // if its a client
                    System.out.println("Recieived connection of type: Client");
                    executorService.submit(() -> {
                        try {
                            Client client = (Client) in.readObject();
                            System.out.println("Asking client for id");
                            int id = client.id;
                            System.out.println("client id" + id);
                            ClientConnection cliConnect = new ClientConnection(client,socketIn,in,out);
                            clientConnects.put(id, client);
                            handleClientConnection(cliConnect);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                }
                }
                //Object msg = "Received connection of " + type + "type.";
                //out.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBankConnection(BankConnection bConnect) throws IOException {
        //int bankID = in.read();
        //System.out.println("received id");
        //double bankFunds = in.read();
        //Bank node = new Bank(bankID,bankFunds); //adds bank to list of banks
        //addBank(node);

        System.out.println("bank added");
        System.out.println("banks available: " + bankConnects.size());
        //bConnect.oStream.writeObject("hello");
    }
    public void handleClientConnection(ClientConnection cConnect) throws IOException, ClassNotFoundException {
        System.out.println("inside handle client method");
        Boolean on = true;
        while(on) {
            Object msg = "Enter 1 to request credit\nEnter 2 to pay back credit\n Enter 3 to see your balance\n Enter 4 to exit";
            cConnect.oStream.writeObject(msg);
            System.out.println("waiting for choice");
            int reqChoice = (int) cConnect.iStream.readObject();
            System.out.println("Choice received " + reqChoice);
            if (reqChoice == 1) { //request credit
                System.out.println("Client selected request credit");
                Object msg2 = "How much money would you like to withdraw?";
                cConnect.oStream.writeObject(msg2);
                System.out.println("prompt msg sent");
                int reqAmount = (int) cConnect.iStream.readObject();
                System.out.println("Received Credit request for " + reqAmount + "dollars");
                System.out.println("processing request");
                boolean canComplete = sendCreditRequest(cConnect.client, reqAmount);
                System.out.println("Back in Handle Client Connection with ans: " + canComplete);
                int code = 0;
                if (canComplete == true) {
                    System.out.println("sending code to client");
                    code = 1;
                    cConnect.oStream.writeObject(code); // process code for completing
                }
                else {
                    cConnect.oStream.writeObject(code); // process code for failure
                }
            }
            else if (reqChoice == 2) { //request payback
                int reqAmount = (int) cConnect.iStream.readObject();
                System.out.println("Received Payback request for " + reqAmount + "dollars");
                System.out.println("processing request");
                double total = sendPaybackRequest(cConnect.client, reqAmount);
                cConnect.oStream.writeDouble(total);
            }
            else if(reqChoice == 3){
                System.out.println("Balance req");
            }
        }
    }
   /** public synchronized void addBank(Bank node){
        bankConnects.add(node);
        System.out.println("node added");
    }
    **/
    public synchronized boolean sendCreditRequest(Client client, int amount) throws IOException, ClassNotFoundException {
        int yesCount = 0;
        int noCount = 0;
        //Map<Bank, Boolean> bankResponses = new HashMap<>(); //yes or no responses from bank
        for (int i = 0; i < bankConnects.size(); i++) { //for each bank process the request then get a yes or no response
            //send code to bank for type of operation
            //then have bank send boolean response
            int code = 0; //Code that leader gives to bank to do operation
            //BankConnection tempBC = new BankConnection();
                bankConnects.get(i).oStream.writeObject(code);
                System.out.println("code sent to bank");

                bankConnects.get(i).oStream.writeObject(amount);
                System.out.println("amount sent to bank");
                bankConnects.get(i).oStream.writeObject(client.id);
                System.out.println("client id sent to bank");

            //now bank will send back yes or no
            System.out.println("getting answer");
            int ans = (int) bankConnects.get(i).iStream.readObject();
            //Object ans = String.valueOf(bankConnects.get(i).iStream.readObject());
            System.out.println("received the following: ");
            System.out.println(ans);
            System.out.println("answer recievied");
            if(ans == 1){
                yesCount++;
                System.out.println("added a yes");
            }
            else{
                noCount++;
            }
        }
        if (yesCount > noCount) {
            System.out.println("Banks said yes!");
            double perBankAmount = amount / yesCount;
            for (int i = 0; i < bankConnects.size(); i++) {
                int code = 1; // to start add debt method in bank
                System.out.println("Sending new choice " + code);
                bankConnects.get(i).oStream.writeObject(code);
                System.out.println("sending perBank amount");
                bankConnects.get(i).oStream.writeObject(perBankAmount);
                System.out.println("Sending Client");
                bankConnects.get(i).oStream.writeObject(client);
                System.out.println("received answer: " + bankConnects.get(i).iStream.readObject());

            }

            System.out.println("The yes's have it");
            return true;
        }
        else {
            System.out.println("Credit request denied for client " + client.id);
        }
        return false;
    }
    public synchronized double sendPaybackRequest(Client client, double amount) throws IOException, ClassNotFoundException {
        int code = 2;
        double moneyLeft = 0;
        //double perBankAmount = amount / bankConnects.size();
        for (int i = 0; i < bankConnects.size(); i++) {
          // status = bankConnects.get(i).paybackRequest(clientId, amount);
            bankConnects.get(i).oStream.writeObject(code);//send choice to bank
            bankConnects.get(i).oStream.writeObject(amount);//send amount
            bankConnects.get(i).oStream.writeObject(client);//send client info
            double ans = (double) bankConnects.get(i).iStream.readObject();
            System.out.println(ans);
            if(ans == 0 || ans > 0){ //returns 0 or with extra money
               moneyLeft += ans;
           }
           else{ // returns w negative debt amount
               moneyLeft -= ans;
           }
        }
        return moneyLeft;
    }
    /**
    public synchronized void processPaybackRequest(String clientId, double amount) {
        double existingDebt = clientToDebt.getOrDefault(clientId, 0.0);
        if (amount > existingDebt) {
            System.out.println("Payback amount exceeds existing debt for client " + clientId);
            return;
        }

        Map<Node, Double> nodeResponses = new HashMap<>();
        for (Node node : banks) {
            double response = node.processPaybackRequest(clientId, amount);
            nodeResponses.put(node, response);
        }

        double totalPayback = nodeResponses.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalPayback > amount) {
            System.out.println("Error: Payback amount exceeded actual debt for client " + clientId);
            return;
        }

        for (Node node : banks) {
            double nodePayback = nodeResponses.get(node);
            if (nodePayback > 0) {
                node.subtractDebt(clientId, nodePayback);
            }
        }

        clientToDebt.put(clientId, existingDebt - amount);
        System.out.println("Payback request processed for client " + clientId + ", remaining debt: " + (existingDebt - amount));
    }

    public synchronized double getClientDebt(String clientId) {
        return clientToDebt.getOrDefault(clientId, 0.0);
    }
**/
    public static void main(String[]args) throws IOException {
    port = 8080;
    try {
        LeaderNode leader = new LeaderNode(port);
        leader.run();
    }
    catch (Exception e){
        e.printStackTrace();
    }
    }

}
