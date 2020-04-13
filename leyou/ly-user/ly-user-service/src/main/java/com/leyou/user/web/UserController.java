package com.leyou.user.web;

import com.leyou.common.exception.LyException;
import com.leyou.user.entity.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 校验数据是否唯一
     * @param data  手机号或者用户名
     * @param type  1： 用户名      2：手机号码
     * @return      true:  可用；   false: 不可用
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable("data") String data, @PathVariable("type") Integer type){
        Boolean bool = userService.check(data, type);
        return ResponseEntity.ok(bool);
    }

    /**
     * 根据用户手机号码发送短信
     * @param phone 用户手机号码
     * @return      无
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户注册
     * @param user   接收参数的实体类
     * @param code   验证码
     * @return       无
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user,  // @Valid ： 使用Hibernate-validtor来校验
                                         BindingResult br,
                                         @RequestParam("code") String code){
        // 捕获异常
        if(br.hasErrors()){
            String errMsg = br.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(" | "));
            throw new LyException(402, errMsg);
        }

        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
