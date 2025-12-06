package org.svids.tbankcooldownapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.svids.tbankcooldownapi.entity.ManualCooling;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManualCoolingRepo extends JpaRepository<ManualCooling, UUID> {
    Optional<ManualCooling> findByUser_Id(UUID userId);
}
