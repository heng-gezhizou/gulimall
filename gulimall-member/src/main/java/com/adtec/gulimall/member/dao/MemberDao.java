package com.adtec.gulimall.member.dao;

import com.adtec.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author hgzz
 * @email 2333@gmail.com
 * @date 2020-09-15 21:49:32
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
