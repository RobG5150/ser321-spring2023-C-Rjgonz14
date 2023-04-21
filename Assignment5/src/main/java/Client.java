//should only have id, money, and debt i believe
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.Serializable;

public class Client implements Serializable {
    protected int id;
    private double moneyAvailable;
    private double debtToPay;

    public Client(int id){
        this.id = id;
        this.debtToPay = 0;
        this.moneyAvailable = 0;
    }

    public void start(String host, int port, Client client){
        try{
            Socket sock = new Socket(host,port);
            System.out.println("Client connection received on port: " + port + "with ID" + client.id);
            synchronized (this) {
                while (true) {
                    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
                    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                    System.out.println("sending type");
                    int type = 1; // type for client
                    out.writeObject(type);
                    System.out.println("type of connection sent");
                    out.writeObject(client);
                    System.out.println("client info sent");
                    Boolean on = true;
                    while(on) {
                        try {
                            System.out.println(in.readObject()); //msg from leader
                            System.out.println("Enter your choice: ");
                        }
                        catch(OptionalDataException e){
                            in.skipBytes(e.length);
                        }
                        int choice = Integer.parseInt(stdin.readLine()); // this is req choice in leader
                        System.out.println("sending choice:" + choice);
                        out.writeObject(choice);
                        System.out.println("choice sent");
                        // now leader will send back response based on choice
                        if (choice == 1) {
                            System.out.println(" in proc 1");
                            System.out.println(in.readObject()); // first msg received
                            int withdrawAmount = Integer.parseInt(stdin.readLine());
                            System.out.println("Sending credit request for amount of: " + withdrawAmount);
                            out.writeObject(withdrawAmount); //this is reqAmount in Leader
                            System.out.println("request sent");
                            int procCode = (int) in.readObject();
                            if (procCode == 1) {
                                System.out.println("Request for " + withdrawAmount + " completed!");
                                debtToPay += withdrawAmount;
                                moneyAvailable += withdrawAmount;
                            }
                            else if (procCode == 0) {
                                System.out.println("Request could not be processed.");
                            }
                        }
                        //sendCreditRequest(ID, amount);

                        else if (choice == 2) { //payback req
                            System.out.println("in proc 2");
                            System.out.println("How much money would you like to pay back?");
                            int pbAmount = Integer.parseInt(stdin.readLine());
                            if(pbAmount > moneyAvailable){
                                System.out.println("You have insufficient funds");
                                System.out.println("You currently have: " + moneyAvailable);
                            }
                            else {
                                System.out.println("Sending payback request for amount of: " + pbAmount);
                                //out.writeInt(1); // number for payback request
                                out.writeObject(pbAmount);
                                int amountBack = 0;
                                try {
                                    amountBack = (int) in.readObject();
                                } catch (OptionalDataException e) {
                                    in.skipBytes(e.length);
                                }
                                if (amountBack == 0) {
                                    System.out.println("Request to pay " + pbAmount + " completed!");
                                    debtToPay -= pbAmount;
                                    moneyAvailable -= pbAmount;
                                    System.out.println("no money left over");
                                }
                                else if (amountBack > 0) {
                                    System.out.println("Request to pay " + pbAmount + " completed!");
                                    debtToPay = 0;
                                    moneyAvailable = amountBack;
                                    System.out.println(amountBack + " money left over");
                                }
                                else {
                                    System.out.println("Request to pay " + pbAmount + " completed!");
                                    debtToPay -= pbAmount;
                                    moneyAvailable -= pbAmount;
                                    System.out.println("client still has to pay: " + debtToPay);
                                }
                            }
                        }
                        else if (choice == 3) {
                            System.out.println(" 3");
                            System.out.println("Retrieving balance details:");
                            System.out.println(moneyAvailable);
                            System.out.print(" Dollars");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        int clientID;
        String host = "localhost";
        int port = 8080;
        System.out.println("Please enter your id: ");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        clientID = Integer.parseInt(stdin.readLine());
        Client newClient = new Client(clientID);
        newClient.start(host,port,newClient);
    }
}

/**
 // Create an instance of CreditProcessor
 CreditProcessor creditProcessor = new CreditProcessor();

 // Process a credit request
 int creditAmount = 500;
 String creditResponse = creditProcessor.processCreditRequest(creditAmount);
 System.out.println(creditResponse);

 // Process a payback request
 int paybackAmount = 200;
 String paybackResponse = creditProcessor.processPaybackRequest(paybackAmount);
 System.out.println(paybackResponse);

 // Add debt
 int debtAmount = 300;
 String addDebtResponse = creditProcessor.addDebt(debtAmount);
 System.out.println(addDebtResponse);

 // Subtract debt
 int subtractDebtAmount = 100;
 String subtractDebtResponse = creditProcessor.subtractDebt(subtractDebtAmount);
 System.out.println(subtractDebtResponse);

 // Send credit response
 String creditResponseMessage = "Credit approved";
 String sendCreditResponse = creditProcessor.sendCreditResponse(creditResponseMessage);
 System.out.println(sendCreditResponse);
 }
 **/