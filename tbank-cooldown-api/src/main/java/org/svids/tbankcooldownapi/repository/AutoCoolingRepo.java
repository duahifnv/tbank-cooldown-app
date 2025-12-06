package org.svids.tbankcooldownapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.svids.tbankcooldownapi.entity.AutoCooling;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AutoCoolingRepo extends JpaRepository<AutoCooling, UUID> {
    Optional<AutoCooling> findByUser_Id(UUID userId);
}
