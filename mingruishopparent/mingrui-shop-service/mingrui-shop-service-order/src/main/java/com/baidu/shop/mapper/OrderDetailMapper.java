package com.baidu.shop.mapper;

import com.baidu.shop.entity.OrderDetailEntity;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

public interface OrderDetailMapper extends Mapper<OrderDetailEntity>, InsertListMapper<OrderDetailEntity> {
}
