package com.adtec.gulimall.ware.service;

import com.adtec.gulimall.ware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-15 22:09:06
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryListPage(Map<String, Object> params);

    List<PurchaseDetailEntity> getPurchaseDetailById(Long id);
}

