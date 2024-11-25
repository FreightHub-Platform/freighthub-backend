package com.freighthub.core.repository;

import com.freighthub.core.dto.RouteDetailsDto;
import com.freighthub.core.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

    @Transactional
    @Query(value = "SELECT r.routeid AS routeId, r.path AS path, r.distance_km AS distanceKm, " +
            "       r.actual_distance_km AS actualDistanceKm, r.cost AS cost, r.estd_cost AS estdCost, " +
            "       r.crane_flag AS craneFlag, r.refrig_flag AS refrigFlag, r.time_minutes AS timeMinutes, r.status AS routeStatus, " +
            "       po.poid AS poId, po.po_number AS poNumber, po.store_name AS storeName, po.drop_date AS dropDate, " +
            "       po.drop_time AS dropTime, po.contact_number AS contactNumber, po.email AS email, po.status AS poStatus, po.address AS address, " +
            "       o.orderid AS orderId, o.order_time AS orderTime, o.pickup_date AS pickupDate, o.from_time AS fromTime, " +
            "       o.to_time AS toTime, o.pickup_location AS pickupLocation, o.status AS orderStatus, " +
            "       SUM(i.weight) AS totalWeight, SUM(i.cbm) AS totalCbm, " +
            "       STRING_AGG(DISTINCT it.type_name, ',') AS itemTypeNames, " + // Added DISTINCT for itemTypeNames
            "       c.business_name AS businessName " +
            "FROM routes r " +
            "JOIN items i ON i.route_id = r.routeid " +
            "JOIN purchase_orders po ON po.poid = i.po_id " +
            "JOIN orders o ON o.orderid = po.order_id " +
            "JOIN consigners c ON c.uid = o.user_id " +
            "JOIN item_types it ON it.i_typeid = i.i_type_id " +
            "WHERE r.routeid = :routeId " +
            "GROUP BY r.routeid, r.path, r.distance_km, r.actual_distance_km, r.cost, r.estd_cost, r.crane_flag, r.refrig_flag, r.time_minutes, r.status, " +
            "         po.poid, po.po_number, po.store_name, po.drop_date, po.drop_time, po.contact_number, po.email, po.status, po.address, " +
            "         o.orderid, o.order_time, o.pickup_date, o.from_time, o.to_time, o.pickup_location, o.status, " +
            "         c.business_name",
            nativeQuery = true)
    Optional<RouteDetailsDto> getRouteDetailsByRouteId(@Param("routeId") Integer routeId);


}