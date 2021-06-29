package com.paypal.bfs.test.employeeserv.impl.dao;

import com.paypal.bfs.test.employeeserv.impl.dao.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity,Integer> {
}
