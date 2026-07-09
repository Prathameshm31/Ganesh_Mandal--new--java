package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.CollectionDTO;
import com.ganesh.mandal.dto.CollectionSummaryDTO;
import com.ganesh.mandal.entity.Collection;
import com.ganesh.mandal.entity.Member;
import com.ganesh.mandal.entity.PaymentMode;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.CollectionRepository;
import com.ganesh.mandal.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<CollectionDTO> getAllCollections() {
        return collectionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollectionDTO> getCollectionsByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member", memberId);
        }
        return collectionRepository.findByMemberIdOrderByCollectionDateDesc(memberId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CollectionDTO createCollection(CollectionDTO dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", dto.getMemberId()));

        PaymentMode mode = parsePaymentMode(dto.getPaymentMode());

        String receiptNumber = dto.getReceiptNumber();
        if (receiptNumber == null || receiptNumber.isBlank()) {
            receiptNumber = "REC-" + System.currentTimeMillis();
        }

        Collection collection = Collection.builder()
                .member(member)
                .amount(dto.getAmount())
                .paymentMode(mode)
                .transactionId(dto.getTransactionId())
                .receiptNumber(receiptNumber)
                .collectorName(dto.getCollectorName())
                .colony(dto.getColony())
                .collectionDate(dto.getCollectionDate())
                .remarks(dto.getRemarks())
                .build();

        Collection saved = collectionRepository.save(collection);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public CollectionDTO getCollectionById(Long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));
        return toDTO(collection);
    }

    @Transactional
    public CollectionDTO updateCollection(Long id, CollectionDTO dto) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", dto.getMemberId()));

        PaymentMode mode = parsePaymentMode(dto.getPaymentMode());

        collection.setMember(member);
        collection.setAmount(dto.getAmount());
        collection.setPaymentMode(mode);
        collection.setTransactionId(dto.getTransactionId());
        collection.setReceiptNumber(dto.getReceiptNumber());
        collection.setCollectorName(dto.getCollectorName());
        collection.setColony(dto.getColony());
        collection.setCollectionDate(dto.getCollectionDate());
        collection.setRemarks(dto.getRemarks());

        Collection saved = collectionRepository.save(collection);
        return toDTO(saved);
    }

    @Transactional
    public void deleteCollection(Long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));
        collectionRepository.delete(collection);
    }

    @Transactional(readOnly = true)
    public Page<CollectionDTO> searchCollections(String memberName, String paymentMode,
                                                   LocalDate startDate, LocalDate endDate,
                                                   int page, int size, String sortBy, String sortOrder) {
        Specification<Collection> spec = Specification.where(null);

        if (memberName != null && !memberName.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("member").get("name")), "%" + memberName.toLowerCase() + "%"));
        }

        if (paymentMode != null && !paymentMode.isBlank()) {
            PaymentMode mode = parsePaymentMode(paymentMode);
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("paymentMode"), mode));
        }

        if (startDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("collectionDate"), startDate));
        }

        if (endDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("collectionDate"), endDate));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return collectionRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public CollectionSummaryDTO getSummary() {
        BigDecimal totalAmount = collectionRepository.sumAllAmount();
        BigDecimal totalCash = collectionRepository.sumByPaymentMode(PaymentMode.CASH);
        BigDecimal totalOnline = collectionRepository.sumByPaymentModeNotIn(List.of(PaymentMode.CASH));
        Long totalMembersContributed = collectionRepository.countDistinctMembers();

        return CollectionSummaryDTO.builder()
                .totalAmount(totalAmount)
                .totalCash(totalCash)
                .totalOnline(totalOnline)
                .totalMembersContributed(totalMembersContributed)
                .build();
    }

    private PaymentMode parsePaymentMode(String mode) {
        if (mode == null) throw new IllegalArgumentException("Payment mode is required");
        String normalized = mode.toUpperCase().replaceAll("\\s+", "_");
        try {
            return PaymentMode.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment mode: " + mode);
        }
    }

    private CollectionDTO toDTO(Collection collection) {
        return CollectionDTO.builder()
                .id(collection.getId())
                .memberId(collection.getMember().getId())
                .memberName(collection.getMember().getName())
                .memberMobile(collection.getMember().getMobile())
                .amount(collection.getAmount())
                .paymentMode(collection.getPaymentMode().name())
                .transactionId(collection.getTransactionId())
                .receiptNumber(collection.getReceiptNumber())
                .collectorName(collection.getCollectorName())
                .colony(collection.getColony())
                .collectionDate(collection.getCollectionDate())
                .remarks(collection.getRemarks())
                .createdAt(collection.getCreatedAt())
                .build();
    }
}
