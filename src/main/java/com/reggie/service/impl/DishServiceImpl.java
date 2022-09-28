package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishMapper;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//涉及两张表，需要开启事务。但同时还有要在启动类加注解
@Transactional
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    //因为要操作dish_flavor，所以需要注入
    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品，同时保存对应的口味
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存基本信息到dish
        this.save(dishDto);
        Long dishId = dishDto.getId();//菜品id，因为dish_flavor表有该字段，但网页未封装
        List<DishFlavor> flavors = dishDto.getFlavors();
        //遍历集合，将数据存至变量item并赋值给原来的flavor集合
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    //根据菜品Id查询菜品信息和口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品信息，从dish表
        Dish dish = this.getById(id);
        //查询口味信息，从dish_flavor表
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品基本信息到dish表
        this.updateById(dishDto);
        //清空菜品口味——dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加新提交的菜品口味——dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        //
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
