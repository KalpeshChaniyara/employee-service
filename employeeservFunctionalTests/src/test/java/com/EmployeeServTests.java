package com;

import com.paypal.bfs.test.employeeserv.EmployeeservApplication;
import com.paypal.bfs.test.employeeserv.api.EmployeeResource;
import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.impl.EmployeeResourceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@SpringBootTest(classes = {EmployeeservApplication.class})
public class EmployeeServTests {

    @Autowired
    EmployeeResource employeeResource;

    @Test
    void name() {
        final ResponseEntity<Integer> createResponseEntity = employeeResource.create(new Employee().withFirstName("Kalpesh")
                .withLastName("Chaniyara")
                .withDateOfBirth(LocalDate.of(1987, 04, 05))
                .withAddress(new Address().withLine1("A 401, Kalpataru Harmony")
                        .withCity("Pune")
                        .withState("Maharashtra")
                        .withCountry("India").withZipCode(411057)));
        Assert.assertEquals(HttpStatus.CREATED.value(), createResponseEntity.getStatusCodeValue());
        Assert.assertTrue(createResponseEntity.getBody() > 0);

        final ResponseEntity<Employee> responseEntity = employeeResource.employeeGetById(createResponseEntity.getBody());
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertEquals("Kalpesh", responseEntity.getBody().getFirstName());
        Assert.assertEquals("Chaniyara", responseEntity.getBody().getLastName());
    }
}
