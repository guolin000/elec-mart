package com.example.entity;

import lombok.Data;

/**
 * @author dxp
 */
@Data
public class AliPay {
    private String traceNo;
    private double totalAmount;
    private String subject;
    private String alipayTraceNo;
}
