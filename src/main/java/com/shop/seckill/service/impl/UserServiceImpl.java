package com.shop.seckill.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.shop.seckill.entity.User;
import com.shop.seckill.dao.UserMapper;
import com.shop.seckill.exception.GlobalException;
import com.shop.seckill.redis.UserKey;
import com.shop.seckill.result.CodeMsg;
import com.shop.seckill.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.seckill.utils.MD5Util;
import com.shop.seckill.utils.RedisUtil;
import com.shop.seckill.utils.UUIDUtil;
import com.shop.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    BloomFilter bloomFilter;

    @Override
    public User getUserById(long id) {
        // 对象缓存
        User user = redisUtil.get(UserKey.getById, "" + id, User.class);
        if (bloomFilter.mightContain(id)){
            if (user != null) {
                return user;
            }else {
                // 双层检测加锁
                synchronized (this) {
                    user = redisUtil.get(UserKey.getById, "" + id, User.class);
                    if (user == null) {
                        // 取数据库
                        user = userMapper.selectById(id);
                        if (user != null) {
                            // 把数据库查询出来的数据放入redis
                            redisUtil.set(UserKey.getById, "" + id, user);
                        } else {
                            System.out.println("发生缓存穿透");
                        }
                    }
                }
            }
        }else {
            System.out.println("该用户不存在！");
        }

        return user;
    }

    /**
     * 典型缓存同步场景：更新密码
     */
    @Override
    public boolean updatePassword(String token, long id, String formPass) {
        // 取user
        User user = getUserById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 更新数据库
        User toBeUpdate = new User();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        userMapper.updateById(toBeUpdate);
        // 更新缓存：先删除再插入
        redisUtil.delete(UserKey.getById, "" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisUtil.set(UserKey.token, token, user);
        return true;
    }

    @Override
    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        // 判断手机号是否存在
        User user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        // 生成唯一id作为token
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    /**
     * 将token做为key，用户信息做为value 存入redis模拟session
     * 同时将token存入cookie，保存登录状态
     */
    @Override
    public void addCookie(HttpServletResponse response, String token, User user) {
        redisUtil.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        // 设置为网站根目录
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 根据token获取用户信息
     */
    @Override
    public User getUserByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisUtil.get(UserKey.token, token, User.class);
        // 延长有效期，有效期等于最后一次操作+有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 从数据库查询所有用户数据
        List<User> list = userMapper.selectList(queryWrapper);
        // 从数据库里拿到的数据加载到布隆过滤器
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), list.size());
        // 把数据放到布隆过滤器里
        for (User user : list) {
            bloomFilter.put(user.getId());
        }
        return list;
    }

    @Override
    public User getUserByName(String nickname) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nickname", nickname);

        return userMapper.selectOne(queryWrapper);
    }
}
