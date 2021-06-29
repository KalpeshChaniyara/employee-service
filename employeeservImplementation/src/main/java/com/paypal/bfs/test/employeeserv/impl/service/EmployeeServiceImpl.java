package com.paypal.bfs.test.employeeserv.impl.service;

import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.impl.dao.EmployeeRepository;
import com.paypal.bfs.test.employeeserv.impl.dao.model.EmployeeEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<Employee> getById(Integer id) {
        final Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(id);
        if (!employeeEntity.isPresent()) {
            return Optional.ofNullable(null);
        }
        final Employee employee = modelMapper.map(employeeEntity.get(), Employee.class);
        return Optional.of(employee);
    }

    @Override
    public Integer create(Employee employee) {
        final EmployeeEntity employeeEntity =
                modelMapper.map(employee, EmployeeEntity.class);
        final EmployeeEntity createdEmployeeEntity = employeeRepository.save(employeeEntity);
        return createdEmployeeEntity.getId();
    }
}
