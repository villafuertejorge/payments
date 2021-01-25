package com.soproen.paymentsmodule.app.repository.term;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.model.term.PayTerm;

@Repository
public interface PayTermRepository extends JpaRepository<PayTerm, Long> {

}
