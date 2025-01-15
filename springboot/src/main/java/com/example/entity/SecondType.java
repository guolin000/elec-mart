package com.example.entity;

import lombok.Data;

import java.io.Serializable;
@Data
public class SecondType implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;

    private String name;
    private String description;
    private String img;
    private Integer typeId;
    private String parentName;
}
