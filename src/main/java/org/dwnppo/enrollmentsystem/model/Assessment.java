package org.dwnppo.enrollmentsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Assessment {

    private String id;
    private String enrollmentId;

    private BigDecimal tuitionFee;
    private BigDecimal miscellaneousFee;
    private BigDecimal laboratoryFee;
    private BigDecimal otherFee;

    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal balance;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Assessment() {
    }

    public Assessment(
            String id,
            String enrollmentId,
            BigDecimal tuitionFee,
            BigDecimal miscellaneousFee,
            BigDecimal laboratoryFee,
            BigDecimal otherFee
    ) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.tuitionFee = tuitionFee;
        this.miscellaneousFee = miscellaneousFee;
        this.laboratoryFee = laboratoryFee;
        this.otherFee = otherFee;

        calculateAmounts();
    }

    public void calculateAmounts() {
        this.totalAmount =
                tuitionFee
                        .add(miscellaneousFee)
                        .add(laboratoryFee)
                        .add(otherFee);

        if (amountPaid == null) {
            amountPaid = BigDecimal.ZERO;
        }

        this.balance = totalAmount.subtract(amountPaid);

        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            status = "PAID";
        } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            status = "PARTIAL";
        } else {
            status = "UNPAID";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public BigDecimal getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(BigDecimal tuitionFee) {
        this.tuitionFee = tuitionFee;
    }

    public BigDecimal getMiscellaneousFee() {
        return miscellaneousFee;
    }

    public void setMiscellaneousFee(BigDecimal miscellaneousFee) {
        this.miscellaneousFee = miscellaneousFee;
    }

    public BigDecimal getLaboratoryFee() {
        return laboratoryFee;
    }

    public void setLaboratoryFee(BigDecimal laboratoryFee) {
        this.laboratoryFee = laboratoryFee;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}