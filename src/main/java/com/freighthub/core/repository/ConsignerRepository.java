package com.freighthub.core.repository;

import com.freighthub.core.entity.Consigner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ConsignerRepository extends JpaRepository<Consigner, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Consigner c SET c.businessName = :businessName, c.brn = :brn WHERE c.id = :id")
    void updateBusinessDetails(int id, String businessName, String brn);

}
