public class CreditProcessor {
    private int creditLimit;
    private int currentCredit;
    private int debt;

    public CreditProcessor() {
        creditLimit = 1000;
        currentCredit = 0;
        debt = 0;
    }

    public String processCreditRequest(int amount) {
        if (currentCredit + amount <= creditLimit) {
            currentCredit += amount;
            return "Credit request processed. Available credit: " + (creditLimit - currentCredit);
        } else {
            return "Credit request denied. Insufficient credit limit.";
        }
    }

    public String processPaybackRequest(int amount) {
        if (currentCredit >= amount) {
            currentCredit -= amount;
            return "Payback request processed. Available credit: " + (creditLimit - currentCredit);
        } else {
            return "Payback request denied. Invalid payback amount.";
        }
    }

    public String addDebt(int amount) {
        debt += amount;
        return "Debt added. Total debt: " + debt;
    }

    public String subtractDebt(int amount) {
        if (debt >= amount) {
            debt -= amount;
            return "Debt subtracted. Total debt: " + debt;
        } else {
            return "Debt subtraction denied. Invalid debt amount.";
        }
    }

    public String sendCreditResponse(String response) {
        // Implementation for sending credit response to external system
        return "Credit response sent: " + response;
    }
}