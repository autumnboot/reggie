package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;
import org.springframework.stereotype.Service;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味。需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);
    //根据菜品Id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
