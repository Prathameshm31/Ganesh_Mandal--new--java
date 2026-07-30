package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Payment;
import com.ganesh.mandal.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByPaymentId(String paymentId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByStatusIn(List<PaymentStatus> statuses);

    long countByStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS'")
    BigDecimal sumSuccessfulAmount();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS' AND p.paymentDate = :date")
    BigDecimal sumSuccessfulByDate(LocalDate date);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS' AND p.paymentDate >= :start AND p.paymentDate < :end")
    BigDecimal sumSuccessfulBetween(LocalDate start, LocalDate end);

    List<Payment> findTop10ByOrderByCreatedAtDesc();
}
