package com.paypal.bfs.test.employeeserv.impl.service;


import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.impl.dao.model.EmployeeEntity;

import java.util.Optional;

public interface EmployeeService {

    Optional<Employee> getById(Integer id);

    Integer create(Employee employee);

}
