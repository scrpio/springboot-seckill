package com.shop.seckill.dao;

import com.shop.seckill.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
