package com;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.bfs.test.employeeserv.EmployeeservApplication;
import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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


    @Test
    void verifyMandatoryLastName() throws Exception {
        final Employee employeeWithMissingLastName = createEmployee("Kalpesh", null, 1987, 04, 05, "A 401, Kalpataru Harmony", "Pune", "Maharashtra", "India", 411057, null);
        expectClientError(performCreateEmployee(employeeWithMissingLastName));
    }

    @Test
    void verifyMandatoryFirstName() throws Exception {
        final Employee employeeWithMissingFirstName = createEmployee(null, "Chaniyara", 1987, 04, 05, "A 401, Kalpataru Harmony", "Pune", "Maharashtra", "India", 411057, null);
        expectClientError(performCreateEmployee(employeeWithMissingFirstName));
    }

    @Test
    void verifyMandatoryDateOfBirth() throws Exception {
        final Employee employeeWithMissingDOB = createEmployee("Kalpesh", "Chaniyara", null, null, null, "A 401, Kalpataru Harmony", "Pune", "Maharashtra", "India", 411057, null);
        expectClientError(performCreateEmployee(employeeWithMissingDOB));
    }

    private ResultActions performCreateEmployee(Employee employee) throws Exception {
        return mockMvc.perform(post("/v1/bfs/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));
    }

    private void expectClientError(ResultActions createEmployeeResponse) throws Exception {
        createEmployeeResponse
                .andExpect(status().is4xxClientError());
    }

    private Employee createEmployee(String firsName, String lastName, Integer yearOfBirth, Integer monthOfBirth,
                                    Integer dayOfBirth, String line1, String city, String state, String country,
                                    int zipCode, String line2) {
        LocalDate dateOfBirth = null;
        if (yearOfBirth != null && monthOfBirth != null && dayOfBirth != null) {
            dateOfBirth = LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth);
        }
        final Employee employee = new Employee()
                .withFirstName(firsName)
                .withLastName(lastName)
                .withDateOfBirth(dateOfBirth)
                .withAddress(new Address()
                        .withLine1(line1)
                        .withLine2(line2)
                        .withCity(city)
                        .withState(state)
                        .withCountry(country)
                        .withZipCode(zipCode));
        return employee;
    }

    @Test
    void positiveTest() throws Exception {
        final Employee employee = createEmployee("Kalpesh", "Chaniyara", 1987, 04, 05, "A 401, Kalpataru Harmony", "Pune", "Maharashtra", "India", 411057, null);
        performCreateEmployee(employee).andExpect(status().isCreated());
    }

    @Test
    void expectNotFoundResponseForMissingEmployee() throws Exception {
        mockMvc.perform(get("/v1/bfs/employees/-1")).andExpect(status().isNotFound());
    }
}
