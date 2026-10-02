package org.dwnppo.enrollmentsystem.service;

import org.dwnppo.enrollmentsystem.model.Assessment;
import org.dwnppo.enrollmentsystem.model.Payment;
import org.dwnppo.enrollmentsystem.repository.AssessmentRepository;
import org.dwnppo.enrollmentsystem.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AssessmentRepository assessmentRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            AssessmentRepository assessmentRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public Payment recordPayment(
            String assessmentId,
            String cashierId,
            BigDecimal amount,
            String paymentMethod,
            String referenceNumber
    ) {

        validateAssessmentId(assessmentId);
        validateCashierId(cashierId);
        validateAmount(amount);
        validatePaymentMethod(paymentMethod);

        Assessment assessment = assessmentRepository
                .findById(assessmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Assessment does not exist."
                        )
                );

        if (assessment.getBalance()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "This assessment has no remaining balance."
            );
        }

        if (amount.compareTo(assessment.getBalance()) > 0) {
            throw new IllegalArgumentException(
                    "Payment cannot exceed the remaining balance."
            );
        }

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                assessmentId,
                cashierId,
                amount,
                paymentMethod,
                referenceNumber
        );

        payment.setStatus("COMPLETED");

        /*
         * Save the payment record first.
         */
        paymentRepository.save(payment);

        /*
         * Update the assessment.
         */
        BigDecimal newAmountPaid =
                assessment.getAmountPaid().add(amount);

        assessment.setAmountPaid(newAmountPaid);
        assessment.calculateAmounts();

        assessmentRepository.update(assessment);

        return payment;
    }

    public Payment getPayment(String paymentId) {

        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Payment ID is required."
            );
        }

        return paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment does not exist."
                        )
                );
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByAssessment(
            String assessmentId
    ) {

        validateAssessmentId(assessmentId);

        return paymentRepository
                .findByAssessmentId(assessmentId);
    }

    public List<Payment> getPaymentsByCashier(
            String cashierId
    ) {

        validateCashierId(cashierId);

        return paymentRepository
                .findByCashierId(cashierId);
    }

    private void validateAssessmentId(String assessmentId) {

        if (assessmentId == null || assessmentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Assessment ID is required."
            );
        }
    }

    private void validateCashierId(String cashierId) {

        if (cashierId == null || cashierId.isBlank()) {
            throw new IllegalArgumentException(
                    "Cashier ID is required."
            );
        }
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null) {
            throw new IllegalArgumentException(
                    "Payment amount is required."
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }
    }

    private void validatePaymentMethod(
            String paymentMethod
    ) {

        if (paymentMethod == null
                || paymentMethod.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment method is required."
            );
        }
    }
}