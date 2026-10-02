package org.dwnppo.enrollmentsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private String id;
    private String assessmentId;
    private String cashierId;

    private BigDecimal amount;

    private String paymentMethod;
    private String referenceNumber;

    private LocalDateTime paidAt;
    private String status;
    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(
            String id,
            String assessmentId,
            String cashierId,
            BigDecimal amount,
            String paymentMethod,
            String referenceNumber
    ) {
        this.id = id;
        this.assessmentId = assessmentId;
        this.cashierId = cashierId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getCashierId() {
        return cashierId;
    }

    public void setCashierId(String cashierId) {
        this.cashierId = cashierId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return id + " - " + amount;
    }
}