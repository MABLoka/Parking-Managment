package system;

public class Payment {
    private int amount;
    private String paymentMethod;

    public Payment(int amount, String paymentMethod) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public int getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}