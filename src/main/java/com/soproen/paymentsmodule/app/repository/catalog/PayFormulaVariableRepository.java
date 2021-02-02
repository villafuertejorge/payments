package com.soproen.paymentsmodule.app.repository.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.model.catalog.PayFormulaVariable;

@Repository
public interface PayFormulaVariableRepository extends JpaRepository<PayFormulaVariable, Long>{

}
