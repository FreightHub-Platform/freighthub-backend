package com.freighthub.core.repository;

import com.freighthub.core.entity.Consigner;
import com.freighthub.core.entity.ReviewBoard;
import com.freighthub.core.entity.User;
import com.freighthub.core.enums.VerifyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsignerRepository extends JpaRepository<Consigner, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Consigner c SET c.businessName = :businessName, c.brn = :brn, c.completion = 1, c.logo = :logoUrl, c.regDoc = :regDoc WHERE c.id = :id")
    void updateBusinessDetails(int id, String businessName, String brn, String logoUrl, byte[] regDoc);

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
    @Query("UPDATE Consigner c SET c.verifyStatus = :verifyStatus, c.verifyTime = :verifyTime WHERE c.id = :id")
    void verifyConsigner(@Param("id") int id, @Param("verifyStatus") VerifyStatus verifyStatus, @Param("verifyTime") LocalDateTime verifyTime);

    @Transactional
    @Query("SELECT c FROM Consigner c WHERE c.verifyStatus = :verifyStatus")
    List<Consigner> findConsignersByVerifyStatus(VerifyStatus verifyStatus);

    @Query("SELECT c.completion FROM Consigner c WHERE c.id = :uid")
    @Transactional
    Integer findCompletionByUid(@Param("uid") Long uid);

    @Query("SELECT c.verifyStatus FROM Consigner c WHERE c.id = :uid")
    @Transactional
    VerifyStatus findVerifyStatusByUid(@Param("uid") Long uid);

    @Query("SELECT c.businessName FROM Consigner c WHERE c.id = :userId")
    Optional<String> findBusinessName(Integer userId);
}
