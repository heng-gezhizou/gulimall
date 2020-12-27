package com.adtec.gulimall.ware.service;

import com.adtec.gulimall.ware.vo.MergeVo;
import com.adtec.gulimall.ware.vo.PurchaseDoneVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-15 22:09:06
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceivePage(Map<String, Object> params);

    void merge(MergeVo mergeVo);

    void receivePurchase(List<Long> ids);

    void done(PurchaseDoneVo vo);
}

