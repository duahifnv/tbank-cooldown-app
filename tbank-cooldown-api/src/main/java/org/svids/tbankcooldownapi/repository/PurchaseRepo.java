package org.svids.tbankcooldownapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.svids.tbankcooldownapi.entity.Purchase;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseRepo extends JpaRepository<Purchase, UUID> {
    List<Purchase> findAllByStatusInAndUser_Id(Collection<PurchaseStatus> statuses, UUID userId);

    Optional<Purchase> findByIdAndUser_Id(UUID id, UUID userId);
}
