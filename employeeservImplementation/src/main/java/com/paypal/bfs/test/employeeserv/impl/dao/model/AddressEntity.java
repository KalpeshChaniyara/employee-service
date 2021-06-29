package com.paypal.bfs.test.employeeserv.impl.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@Embeddable
public class AddressEntity {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private Integer zipCode;
}
