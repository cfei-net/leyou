package com.leyou.user.web;

import com.leyou.common.exception.LyException;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

@Slf4j
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
    @ApiOperation(value = "校验用户名数据是否可用，如果不存在则可用")
    @ApiResponses({
            @ApiResponse(code = 200, message = "校验结果有效，true或false代表可用或不可用"),
            @ApiResponse(code = 400, message = "请求参数有误，比如type不是指定值")
    })
    public ResponseEntity<Boolean> check(
            @ApiParam(value = "要校验的数据：手机号或者用户名", example = "用户名：lisi 或者 手机号 13800138000")
            @PathVariable("data") String data,

            @ApiParam(value = "数据类型，1：用户名，2：手机号", example = "1")
            @PathVariable("type") Integer type){
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


    @Autowired
    private HttpServletRequest request;

    /**
     * 根据用户名和密码查询用户信息
     * @param username      用户名
     * @param password      密码
     * @return              返回用户的DTO
     */
    @GetMapping("/query")
    public ResponseEntity<UserDTO> queryUserByUsernameAndPassword(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ){
        log.info("【用户微服务】获取请求头信息，privilege_token = {}", request.getHeader("privilege_token"));
        // 查询用户
        UserDTO userDTO = userService.queryUserByUsernameAndPassword(username, password);
        return ResponseEntity.ok(userDTO);
    }
}
