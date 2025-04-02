package com.cuzz.common.rookiepay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RookiePayTryOrderResponse implements Serializable {
    private static final long serialVersionUID = 8849649245827190181L;

    private String qrcode;

    private String buyer;

    private Double price;

    private String orderId;
}
