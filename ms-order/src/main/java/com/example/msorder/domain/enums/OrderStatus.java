package com.example.msorder.domain.enums;

import com.example.msorder.rest.services.exceptions.ObjectNotFoundException;

public enum OrderStatus {

    WAITING_CONFIRMATION(1),
    ORDER_CONFIRMED(2),
    ORDER_IS_OUT_FOR_DELIVERY(3),
    ORDER_DELIVERED(4),
    CANCELED(5);

    private final int cod;

    private OrderStatus(int cod){
        this.cod= cod;
    }

    public int getCode(){
        return  cod;
    }

    public static OrderStatus codOf(int cod){
        for(OrderStatus x : OrderStatus.values()){
            if(x.getCode() == cod){
                return x;
            }
        }
        throw new ObjectNotFoundException("the type cod is invalid");
    }
}
