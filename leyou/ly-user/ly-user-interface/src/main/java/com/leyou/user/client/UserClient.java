package com.leyou.user.client;

import com.leyou.user.dto.AddressDTO;
import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {

    /**
     * 根据用户名和密码查询用户信息
     * @param username      用户名
     * @param password      密码
     * @return              返回用户的DTO
     */
    @GetMapping("/query")
    public UserDTO queryUserByUsernameAndPassword(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    );

    /**
     * 跟用户id和地址id查询地址信息
     * @param userId    用户ID
     * @param id        地址的id
     * @return          返回地址DTO
     */
    @GetMapping("/address")
    public AddressDTO queryAddressById(@RequestParam("userId") Long userId,
                                                       @RequestParam("id") Long id);
}
