package com;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.bfs.test.employeeserv.EmployeeservApplication;
import com.paypal.bfs.test.employeeserv.api.EmployeeResource;
import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.impl.EmployeeResourceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@AutoConfigureMockMvc
@SpringBootTest(classes = {EmployeeservApplication.class})
public class EmployeeServTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EmployeeResource employeeResource;

    @Test
    void verifyMandatoryLastName() throws Exception {
        final Employee employeeWithMissingLastName = createEmployee("Kalpesh", null, 1987, 04, 05, "A 401, Kalpataru Harmony", "Pune", "Maharashtra", "India", 411057);
        performCreateEmployeeAndExpectClientError(employeeWithMissingLastName);
    }

    @Test
    void verifyMandatoryFirstName() throws Exception {
        final Employee employeeWithMissingLastName = createEmployee(null, "Chaniyara", 1987, 04, 05, "A 401, Kalpataru Harmony", "Pune", "Maharashtra", "India", 411057);
        performCreateEmployeeAndExpectClientError(employeeWithMissingLastName);
    }

    @Test
    void verifyMandatoryDateOfBirth() throws Exception {
        final Employee employeeWithMissingLastName = createEmployee("Kalpesh", "Chaniyara", null, null, null, "A 401, Kalpataru Harmony", "Pune", "Maharashtra", "India", 411057);
        performCreateEmployeeAndExpectClientError(employeeWithMissingLastName);
    }

    private void performCreateEmployeeAndExpectClientError(Employee employeeWithMissingLastName) throws Exception {
        mockMvc.perform(post("/v1/bfs/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeWithMissingLastName)))
                .andExpect(status().is4xxClientError());
    }

    private Employee createEmployee(String firsName, String lastName, Integer yearOfBirth, Integer monthOfBirth, Integer dayOfBirth, String line1, String city, String state, String country, int zipCode) {
        LocalDate dateOfBirth = null;
        if (yearOfBirth != null && monthOfBirth != null && dayOfBirth != null) {
            dateOfBirth = LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth);
        }
        final Employee employeeWithMissingLastName = new Employee()
                .withFirstName(firsName)
                .withLastName(lastName)
                .withDateOfBirth(dateOfBirth)
                .withAddress(new Address().withLine1(line1)
                        .withCity(city)
                        .withState(state)
                        .withCountry(country)
                        .withZipCode(zipCode));
        return employeeWithMissingLastName;
    }

    @Test
    void positiveTest() {
        final Employee employee = createEmployee("Kalpesh", "Chaniyara", 1987, 04, 05, "A 401, Kalpataru Harmony", "Pune", "Maharashtra", "India", 411057);
        final ResponseEntity<Integer> createResponseEntity = employeeResource.create(employee);
        Assert.assertEquals(HttpStatus.CREATED.value(), createResponseEntity.getStatusCodeValue());
        Assert.assertTrue(createResponseEntity.getBody() > 0);

        final ResponseEntity<Employee> responseEntity = employeeResource.employeeGetById(createResponseEntity.getBody());
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assert.assertEquals("Kalpesh", responseEntity.getBody().getFirstName());
        Assert.assertEquals("Chaniyara", responseEntity.getBody().getLastName());
    }

    @Test
    void expectNotFoundResponseForMissingEmployee() {
        final ResponseEntity<Integer> createResponseEntity = employeeResource.create(new Employee().withFirstName("Kalpesh")
                .withLastName("Chaniyara")
                .withDateOfBirth(LocalDate.of(1987, 04, 05))
                .withAddress(new Address().withLine1("A 401, Kalpataru Harmony")
                        .withCity("Pune")
                        .withState("Maharashtra")
                        .withCountry("India").withZipCode(411057)));
        Assert.assertEquals(HttpStatus.CREATED.value(), createResponseEntity.getStatusCodeValue());
        Assert.assertTrue(createResponseEntity.getBody() > 0);

        final ResponseEntity<Employee> responseEntity = employeeResource.employeeGetById(-1);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
