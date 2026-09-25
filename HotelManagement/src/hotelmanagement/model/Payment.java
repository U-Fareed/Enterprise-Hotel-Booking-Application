/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagement.model;

/**
 *
 * @author USER
 */
public class Payment {
    private int paymentId;
    private int bookingId;
    private double amount;
    private String method;     
    private String status;     
    private String reference;  
    private String paidAt;     

    public Payment() {}

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int v) { this.paymentId = v; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int v) { this.bookingId = v; }

    public double getAmount() { return amount; }
    public void setAmount(double v) { this.amount = v; }

    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }

    public String getReference() { return reference; }
    public void setReference(String v) { this.reference = v; }

    public String getPaidAt() { return paidAt; }
    public void setPaidAt(String v) { this.paidAt = v; }
}
