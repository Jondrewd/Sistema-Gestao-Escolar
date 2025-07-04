package com.api.gestaoescolar.dtos;

public class TypedUserDTO {

    private final String type;
    private final Object data;

    public TypedUserDTO(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
