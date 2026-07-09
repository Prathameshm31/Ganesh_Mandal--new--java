package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Collection;
import com.ganesh.mandal.entity.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long>, JpaSpecificationExecutor<Collection> {

    List<Collection> findByMemberIdOrderByCollectionDateDesc(Long memberId);

    long countByMemberId(Long memberId);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Collection c")
    BigDecimal sumAllAmount();

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Collection c WHERE c.paymentMode = :mode")
    BigDecimal sumByPaymentMode(PaymentMode mode);

    @Query("SELECT COUNT(DISTINCT c.member.id) FROM Collection c")
    Long countDistinctMembers();

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Collection c WHERE c.colony = :colony")
    BigDecimal sumByColony(String colony);

    @Query(value = "SELECT DATE_FORMAT(collection_date, '%Y-%m') as month, SUM(amount) as total FROM collections GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> findMonthlyCollection();

    @Query(value = "SELECT DATE_FORMAT(collection_date, '%Y') as year, SUM(amount) as total FROM collections GROUP BY year ORDER BY year", nativeQuery = true)
    List<Object[]> findYearlyTrend();

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Collection c WHERE c.collectionDate = :date")
    BigDecimal sumByDate(LocalDate date);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Collection c WHERE c.collectionDate >= :start AND c.collectionDate < :end")
    BigDecimal sumByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Collection c WHERE c.paymentMode NOT IN :modes")
    BigDecimal sumByPaymentModeNotIn(List<PaymentMode> modes);

    @Query("SELECT DISTINCT c.member.id FROM Collection c WHERE YEAR(c.collectionDate) = :year")
    List<Long> findMemberIdsWithCollectionInYear(int year);

    List<Collection> findFirst5ByOrderByCollectionDateDesc();

    @Query(value = "SELECT colony, COALESCE(SUM(amount), 0) as total FROM collections WHERE colony IS NOT NULL GROUP BY colony ORDER BY total DESC", nativeQuery = true)
    List<Object[]> findColonyWiseCollection();

    @Query(value = "SELECT payment_mode, COALESCE(SUM(amount), 0) as total FROM collections GROUP BY payment_mode ORDER BY total DESC", nativeQuery = true)
    List<Object[]> findPaymentModeBreakdown();

    @Query(value = "SELECT c.member_id, m.name, COALESCE(SUM(c.amount), 0) as total FROM collections c JOIN members m ON c.member_id = m.id GROUP BY c.member_id, m.name ORDER BY total DESC LIMIT ?1", nativeQuery = true)
    List<Object[]> findTopDonors(int limit);
}
