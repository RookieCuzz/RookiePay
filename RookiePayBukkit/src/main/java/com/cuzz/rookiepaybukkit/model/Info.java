package com.cuzz.rookiepaybukkit.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString
public class Info {
    //付款者UUID
    private String buyerName;
    private String payment;
    private String description;
    private Integer productAmount;

    private List<String> productList;

    private String userUUID;

    //元
    private Double money;

    private String userName;
}
