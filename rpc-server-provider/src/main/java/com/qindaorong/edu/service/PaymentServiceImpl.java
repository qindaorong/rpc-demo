package com.qindaorong.edu.service;

import com.qindaorong.edu.annotations.RpcService;
import com.qindaorong.edu.api.IPaymentService;

@RpcService(value = IPaymentService.class,version = "v1.0")
public class PaymentServiceImpl implements IPaymentService {
    @Override
    public void doPay() {
        System.out.println("执行doPay方法");
    }
}
