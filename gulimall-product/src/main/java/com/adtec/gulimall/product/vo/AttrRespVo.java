package com.adtec.gulimall.product.vo;

import com.adtec.gulimall.product.entity.AttrEntity;
import lombok.Data;

@Data
public class AttrRespVo extends AttrEntity {
    private Long attrGroupId;
    private String catelogName;
    private String groupName;
    private Long catelogId;
    private Long[] catelogPath;

}
