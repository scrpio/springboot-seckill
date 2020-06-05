package com.shop.seckill.service;

import com.shop.seckill.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.seckill.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
public interface UserService extends IService<User> {
    String COOKIE_NAME_TOKEN = "token";

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    User getUserById(long id);

    /**
     * 典型缓存同步场景：更新密码
     */
    boolean updatePassword(String token, long id, String formPass);

    String login(HttpServletResponse response, LoginVo loginVo);

    /**
     * 将token做为key，用户信息做为value 存入redis模拟session
     * 同时将token存入cookie，保存登录状态
     */
    void addCookie(HttpServletResponse response, String token, User user);

    /**
     * 根据token获取用户信息
     */
    User getUserByToken(HttpServletResponse response, String token);
}
