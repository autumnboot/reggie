package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //新增套餐，同时保存套餐和菜品的关联关系
    void saveWithDish(SetmealDto setmealDto);
    void removeWithDish(List<Long> ids);
}
