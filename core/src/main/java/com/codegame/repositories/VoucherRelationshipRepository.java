package com.codegame.repositories;

import com.codegame.model.VoucherRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRelationshipRepository extends JpaRepository<VoucherRelationship, Long> {

}
