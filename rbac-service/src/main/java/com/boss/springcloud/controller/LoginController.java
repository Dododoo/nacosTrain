package com.boss.springcloud.controller;

import com.boss.springcloud.entity.po.MenuPO;
import com.boss.springcloud.entity.dto.UserDTO;
import com.boss.springcloud.entity.pojo.LoginRequest;
import com.boss.springcloud.entity.pojo.LoginResponse;
import com.boss.springcloud.entity.vo.MenuVO;
import com.boss.springcloud.service.MenuService;
import com.boss.springcloud.service.ResponseResult;
import com.boss.springcloud.service.UserService;
import com.boss.springcloud.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Value("${secreteKey}")
    private String secreteKey;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseResult login(@Validated LoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseResult.error(ResponseCodeEnum.PARAMETER_ILLEGAL.getCode(),ResponseCodeEnum.PARAMETER_ILLEGAL.getMessage());
        }
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        //查询登录用户是否存在
        UserDTO user = userService.getUserByName(username, RbacConstant.EFFECTIVE_USER);
        if (user != null && CommenUtil.md5(password).equals(user.getUserPassword())) {
            //用户存在就生成token
            String token = JWTUtil.generateToken(username, secreteKey);
            String refreshToken = CommenUtil.generateUUID();
            //将token放入到redis缓存中
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            hashOperations.put(username, "token", token);
            hashOperations.put(username,"refreshToken", refreshToken);
            //将该用户对应权限的菜单也放到redis里面
            List<MenuVO> listMenu = menuService.listMenu(user.getAuthorityId());
            redisUtil.storeMenu(username, listMenu);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setUsername(username);
            loginResponse.setToken(token);
            loginResponse.setRefreshToken(refreshToken);
            return ResponseResult.success(loginResponse);
        }
        return ResponseResult.error(ResponseCodeEnum.LOGIN_ERROR.getCode(), ResponseCodeEnum.LOGIN_ERROR.getMessage());
    }

    @PostMapping("/logout")
    public ResponseResult logout(@RequestParam String username) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String token = hashOperations.get(username, "token");
        redisTemplate.opsForHash().delete(username);
        return ResponseResult.success();
    }
}
