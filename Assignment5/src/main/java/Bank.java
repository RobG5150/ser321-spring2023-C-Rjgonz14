import org.w3c.dom.Node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
//change to bank, bank has initial amnt of money and id
public class Bank implements Serializable{

    protected final int id;
    private static double initialMoney;
    private double availableMoney;
    private final Map<Integer, Double> clientDebt = new HashMap<>();

    public Bank(int id, double initialMoney) {
        this.id = id;
        this.initialMoney = initialMoney;
        this.availableMoney = initialMoney;
    }


    public void start(String host, int port, Bank bank) throws IOException {
        try {
                Socket sock = new Socket(host,port);
                System.out.println("Bank Node received on port: " + port + " on host: " + host);
                //BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            synchronized(this) {
                while(true){
                System.out.println("test");
                ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                System.out.println("Sending type");
                int type = 0;//0 designates bank node
                out.writeObject(type);
                System.out.println("type of connection sent");
                out.writeObject(bank);
                //out.write(id);
                System.out.println("bank sent");
                //out.write((int) amount);
                //System.out.println("amount sent");

                Boolean on = true;
                int choice = 0;
                while(on) {
                    try {
                        System.out.println("Waiting for Response");
                        //System.out.println(in.readObject());
                        choice = (int) in.readObject();
                    }
                    catch (OptionalDataException e){
                        in.skipBytes(e.length);
                    }
                    System.out.println("choice received " + choice);
                    //Credit request
                    if (choice == 0) {
                        System.out.println("selected 0");
                        //read in amount
                        int amount = (int) in.readObject();
                        System.out.println("received amount");
                        Client client = new Client((Integer) in.readObject());
                        System.out.println("received client");
                        boolean ans = processCreditRequest(client, amount); //get a boolean val that returns to leader
                        System.out.println("Answer is " + ans);
                        int code;
                        if (ans == true) {
                            code = 1;
                            out.writeObject(code); //writing bank ans to leader
                            System.out.println("answer code sent " + code);
                        }
                        else {
                            code = 2;
                            out.writeObject(code);
                            System.out.println("answer code sent " + code);
                        }
                    }
                    //for adding debt
                    else if (choice == 1) {
                        System.out.println("selected 1");
                        double amount = (double) in.readObject();
                        System.out.println("Received amount" + amount);
                        Client client = (Client) in.readObject();
                        System.out.println("received client");
                        Boolean done = addDebt(client, amount);
                        int response;
                        if (done == true) {
                            response = 1;
                            System.out.println("sending msg");
                            out.writeObject(response);
                            System.out.println("msg sent");
                        }
                        else {
                            response = 0;
                            out.writeObject(response);
                        }
                    }
                    //payback req
                    else if (choice == 2){
                        System.out.println("selected 2");
                        double amount = (double) in.readObject();
                        System.out.println("Received amount" + amount);
                        Client client = (Client) in.readObject();
                        Double req = paybackRequest(client,amount);
                        out.writeObject(req);
                    }
                }
                    }
                }
                //while (true) {

                //  in.readObject();
                //out.write(id);
                //double bankFunds = amount;
                //out.writeObject(bankFunds);
                //out.close();
                //in.close();
                //sock.close();
                // }

            }
                catch(Exception e){
                    e.printStackTrace();
                }
    }

    protected boolean processCreditRequest(Client client, int amount){
        //boolean canGrantCredit = !debt.containsKey(request.getClientId())
          //      && availableMoney >= request.getAmount() * 1.5;
        //out.writeObject(new CreditResponse(canGrantCredit));
        boolean canGiveCredit = false; // check if client is in debt list already
        if(clientDebt.containsKey(id)){
            System.out.println("Client already has debt");
        }
        else{
            if(amount < availableMoney){
                canGiveCredit = true;
                return canGiveCredit;
            }
        }
        return canGiveCredit;
    }
    protected boolean addDebt(Client client, double amount){
        if(availableMoney < amount){
            System.out.println("Cannot Provide Money! Insufficient Funds!");
            return false;
        }
        else{
            clientDebt.put(client.id,amount);
            availableMoney = availableMoney - amount;
            System.out.println("Money left in bank: " + availableMoney);
        }
        return true;
    }
    protected double paybackRequest(Client client,double amount ){
        //going to return an int num either 1, 2, or 3
        //1 is payment went thru, client still owes
        //2 is payment went thru, client doesnt owe and possibly refund
        //3 payment did not go through or debt does not exist
        double moneyLeft = 0;
            if(clientDebt.containsKey(client.id)){ //checks if user already has debt
                double moneyOwed = clientDebt.get(client.id);
                System.out.println("money owed" + moneyOwed);
                System.out.println("client owes: " + moneyOwed);
                System.out.println("and would like to pay" + amount);
                moneyLeft = moneyOwed - amount;
                if(moneyLeft > 0){
                    System.out.println("Payment Processed\n Client now owes" + moneyLeft);
                    clientDebt.replace(client.id,moneyOwed,moneyLeft);
                    return moneyLeft * -1; //return negative so leader knows client owes money
                }
                else if(moneyLeft < 0){
                    moneyLeft *= -1;
                    System.out.println("Payment Processed\n");
                    System.out.println("Client sent too much. Refunding extra money");
                    System.out.println("Client no longer has debt");
                    clientDebt.remove(client.id);
                    //code = 2;
                    return moneyLeft; // returning extra money
                }
                else if(moneyLeft == 0){
                    System.out.println("Payment Processed\n");
                    System.out.println("Client no longer has debt");
                    clientDebt.remove(client.id);
                    //code = 2;
                    return moneyLeft;
                }
            }
            else{
                System.out.println("client does not have debt at this bank");
                //code = 3;
                //return code;
                return amount;
            }
            return amount;
    }
    /**
    private void handlePaybackRequest(PaybackRequest request, ObjectOutputStream out) throws IOException {
        int currentDebt = debt.getOrDefault(request.getClientId(), 0);
        int paybackAmount = Math.min(request.getAmount(), currentDebt);
        debt.put(request.getClientId(), currentDebt - paybackAmount);

        int splitAmount = splitPaybackAmount(paybackAmount);
        availableMoney += splitAmount;
        out.writeObject(new PaybackResponse(paybackAmount - splitAmount));
    }

    private void handleBalanceRequest(ObjectOutputStream out) throws IOException {
        out.writeObject(new BalanceResponse(availableMoney, debt));
    }

    private int splitPaybackAmount(int paybackAmount) {
        int nodeCount = 2; // change this if you have more than 2 nodes
        return (paybackAmount * 2) / (3 * nodeCount);
    }
    **/
    public static void main(String[]args) throws IOException{
        if(args.length != 2){
            System.out.println("Usage: gradle node -Pmoney=1000 -Pid=15"); // Pmoney can be whatever num you prefer
            System.exit(0);
        }
        else{
            double init = Double.parseDouble(args[0]);
            int bankID = Integer.parseInt(args[1]);
            Bank node = new Bank(bankID, init);
            String host = "localhost";
            int port = 8080;
            node.start(host, port, node);
        }

    }
}
