package com.adtec.common.to;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundTo {
    @TableId
    private Long id;
    private Long spuId;
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    private Integer work;
}
