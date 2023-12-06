package com.example.msstore.domain.enums;

import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;

public enum TypeCode {

    PHARMACY(1),
    SUPERMARKET(2),
    RESTAURANT(3);

    private final int cod;

    private TypeCode(int cod){
        this.cod= cod;
    }

    public int geCode(){
        return  cod;
    }

    public static TypeCode codOf(int cod){
        for(TypeCode x : TypeCode.values()){
            if(x.geCode() == cod){
                return x;
            }
        }
        throw new ObjectNotFoundException("the type cod is invalid");
    }


}
