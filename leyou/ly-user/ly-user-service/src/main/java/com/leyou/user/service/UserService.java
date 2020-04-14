package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKey.VERIFY_CODE_KEY;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 校验数据是否唯一
     */
    public Boolean check(String data, Integer type) {
        // 封装查询条件
        User record = new User();
        // 判断
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        // count==0  : true
        // 其他情况都是 false
        return userMapper.selectCount(record) == 0;
    }

    private final static String KEY_PREFIX = "user:verify:code:";

    /**
     * 发送短信验证码
     * @param phone
     */
    public void sendCode(String phone) {
        // 1、判断手机号码的正确性
        if(!RegexUtils.isPhone(phone)){
            throw new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }
        // 2、生成随机6位数的验证码
        String code = RandomStringUtils.randomNumeric(6);
        // 3、存入Redis中
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        // 4、往队列中发送短信
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME, VERIFY_CODE_KEY, msg);
    }

    /**
     * 用户注册
     * @param user  用户实体类
     * @param code  页面传入的验证码
     */
    public void register(User user, String code) {
        // 1、从redis中取出验证码比对
        // 1.1 从redis中取出
        String cacheVerifyCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        // 1.2 比对两个验证码是否一致
        if(!StringUtils.equals(code, cacheVerifyCode)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        // 2、对密码进行加密
        // 2.1 判断
        if(StringUtils.isBlank(user.getPassword())){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        // 2.2 用户密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 3、插入数据库
        int count = userMapper.insertSelective(user);
        // 4、严谨性校验
        if(count != 1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据用户名和密码去查询用户
     */
    public UserDTO queryUserByUsernameAndPassword(String username, String password) {
        // 1、根据用户名查询用户信息
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);
        // 2、判断
        if(user == null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        // 3、如果用户不为空，才去判断密码是否正确
        // 参数一：明文
        // 参数二：数据库中密码的密文
        // 返回值： true代表两者一致  ； false不一致
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        // 4、转成DTO返回
        return BeanHelper.copyProperties(user, UserDTO.class);
    }
}
