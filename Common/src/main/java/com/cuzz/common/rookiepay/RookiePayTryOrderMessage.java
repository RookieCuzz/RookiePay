package com.cuzz.common.rookiepay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RookiePayTryOrderMessage implements Serializable {
    private static final long serialVersionUID = 8849649245827190181L;
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
