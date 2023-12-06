package com.example.msuser.domain.enums;

import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;

public enum TypeCard {
    CREDIT_CARD(1),
    DEBIT_CARD(2);

    private final int cod;

    private TypeCard(int cod){
        this.cod = cod;
    }

    public int getCode(){
        return  cod;
    }

    public static TypeCard codOf(int cod){
        for(TypeCard x : TypeCard.values()){
            if(x.getCode() == cod){
                return x;
            }
        }
        throw new ObjectNotFoundException("the type cod is invalid");
    }
}
