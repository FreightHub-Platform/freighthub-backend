package com.freighthub.core.repository;

import com.freighthub.core.entity.FleetOwner;
import com.freighthub.core.entity.ReviewBoard;
import com.freighthub.core.enums.VerifyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FleetOwnerRepository extends JpaRepository<FleetOwner, Integer> {

    @Query("SELECT f.completion FROM FleetOwner f WHERE f.id = :uid")
    @Transactional
    Integer findCompletionByUid(@Param("uid") Long uid);

    @Modifying
    @Transactional
    @Query("UPDATE FleetOwner fo SET fo.companyName = :companyName, fo.brn = :brn, fo.completion = 1 WHERE fo.id = :id")
    void updateBusinessDetails(int id, String companyName, String brn);

    @Modifying
    @Transactional
    @Query("UPDATE FleetOwner fo SET fo.mainNumber = :mainNumber, fo.altNumber = :altNumber, fo.completion = 2 WHERE fo.id = :id")
    void updateContactDetails(int id, String mainNumber, String altNumber);

    @Modifying
    @Transactional
    @Query("UPDATE FleetOwner fo SET fo.addressLine1 = :addressLine1, fo.addressLine2 = :addressLine2, fo.city = :city, fo.province = :province, fo.postalCode = :postalCode, fo.completion = 3 WHERE fo.id = :id")
    void updateLocationDetails(int id, String addressLine1, String addressLine2, String city, String province, String postalCode);

    @Modifying
    @Transactional
    @Query("UPDATE FleetOwner c SET c.verifyStatus = :verifyStatus, c.reviewBoardId = :reviewBoardId, c.verifyTime = :verifyTime WHERE c.id = :id")
    void verifyFleetOwner(@Param("id") int id, @Param("verifyStatus") VerifyStatus verifyStatus, @Param("reviewBoardId") ReviewBoard reviewBoardId, @Param("verifyTime") LocalDateTime verifyTime);

    @Transactional
    @Query("SELECT f FROM FleetOwner f WHERE f.verifyStatus = :verifyStatus")
    List<FleetOwner> findFleetOwnersByVerifyStatus(VerifyStatus verifyStatus);

}