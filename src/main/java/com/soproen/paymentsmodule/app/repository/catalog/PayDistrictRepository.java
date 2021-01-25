package com.soproen.paymentsmodule.app.repository.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;

@Repository
public interface PayDistrictRepository extends JpaRepository<PayDistrict,Long>{

}
