package com.example.mapper;

import com.example.entity.SecondType;

import java.util.List;

public interface SecondTypeMapper {
    List<SecondType> selectAll(SecondType secondType);
    List<SecondType> selectByTypeId(Integer typeId);
    SecondType selectById(Integer id);
    void insert(SecondType secondType);
    void updateById(SecondType secondType);
    void deleteById(Integer id);
    int countByName(String name);
}
