package com.cuzz.rookiepay;

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
public class RookiePayMessage implements Serializable {
    private static final long serialVersionUID = 8849649245827190181L;

    String player;
    String status;
    String orderId;
}
