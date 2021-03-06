package com.adtec.gulimall.product.service;

import com.adtec.gulimall.product.entity.SpuInfoDescEntity;
import com.adtec.gulimall.product.vo.savo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.adtec.common.utils.PageUtils;
import com.adtec.gulimall.product.entity.SpuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-11 17:31:19
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void up(Long spuId);
}

