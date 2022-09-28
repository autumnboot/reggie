package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    //根据id删除分类，但删除之前需要判断是否已关联菜品或套餐
    @Override
    public void remove(Long id) {
        //构造查询条件
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishQueryWrapper);
        if(dishCount > 0){
            //当前要删除的分类存在关联菜品，需抛出业务异常（自定义）
            throw new CustomException("当前分类已关联菜品，无法删除！");
        }
        //构造查询条件
        LambdaQueryWrapper<Setmeal> SetmealQueryWrapper = new LambdaQueryWrapper<>();
        SetmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int SetmealCount = dishService.count(dishQueryWrapper);
        if(SetmealCount > 0){
            //当前要删除的分类存在关联套餐，需抛出业务异常（自定义）
            throw new CustomException("当前分类已关联套餐，无法删除！");
        }
        //未关联任何菜品或套餐，直接删除
        super.removeById(id);
    }
}
