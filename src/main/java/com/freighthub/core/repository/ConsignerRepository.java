package com.freighthub.core.repository;

import com.freighthub.core.entity.Consigner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ConsignerRepository extends JpaRepository<Consigner, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Consigner c SET c.businessName = :businessName, c.brn = :brn, c.completion = 1 WHERE c.id = :id")
    void updateBusinessDetails(int id, String businessName, String brn);

    @Modifying
    @Transactional
    @Query("UPDATE Consigner c SET c.mainNumber = :mainNumber, c.altNumber = :altNumber, c.completion = 2 WHERE c.id = :id")
    void updateContactDetails(int id, String mainNumber, String altNumber);

    @Modifying
    @Transactional
    @Query("UPDATE Consigner c SET c.addressLine1 = :addressLine1, c.addressLine2 = :addressLine2, c.city = :city, c.province = :province, c.postalCode = :postalCode, c.completion = 3 WHERE c.id = :id")
    void updateLocationDetails(int id, String addressLine1, String addressLine2, String city, String province, String postalCode);

    @Modifying
    @Transactional
    @Query("UPDATE Consigner c SET c.verifyStatus = true WHERE c.id = :id")
    void verifyConsigner(int id);

    @Query("SELECT c FROM Consigner c WHERE c.verifyStatus = false")
    List<Consigner> findUnverifiedConsigners();

    @Query("SELECT c.completion FROM Consigner c WHERE c.id = :uid")
    Integer findCompletionByUid(@Param("uid") Long uid);

}
