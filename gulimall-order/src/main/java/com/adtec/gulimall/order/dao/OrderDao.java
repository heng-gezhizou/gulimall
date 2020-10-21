package com.adtec.gulimall.order.dao;

import com.adtec.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-15 22:02:52
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
