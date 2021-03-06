package com.adtec.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.product.entity.CommentReplayEntity;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:20
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

