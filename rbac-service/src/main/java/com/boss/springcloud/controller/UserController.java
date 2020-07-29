package com.boss.springcloud.controller;

import com.boss.springcloud.annotation.AuthRequired;
import com.boss.springcloud.annotation.LoginRequired;
import com.boss.springcloud.entity.po.MenuPO;
import com.boss.springcloud.entity.dto.UserDTO;
import com.boss.springcloud.entity.po.UserPO;
import com.boss.springcloud.entity.vo.MenuVO;
import com.boss.springcloud.service.MenuService;
import com.boss.springcloud.service.UserService;
import com.boss.springcloud.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(@RequestParam String userName, @RequestParam String userPassword,
                           @RequestParam int userAge, @RequestParam String userPhone,
                           @RequestParam int roleId) {
        String userId = CommenUtil.generateUUID();
        UserPO userPO = new UserPO();
        userPO.setUserId(userId);
        userPO.setUserName(userName);
        userPO.setUserPassword(CommenUtil.md5(userPassword));
        userPO.setUserAge(userAge);
        userPO.setCreateTime(new Date());
        userPO.setStatus(RbacConstant.EFFECTIVE_USER);
        userPO.setRoleId(roleId);
        userPO.setUserPhone(userPhone);
        int flag = userService.addUser(userPO);
        String msg = (flag == 0) ? "插入失败" : "插入成功";
        return msg;
    }

    /**
     * login
     * */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String userName,@RequestParam String userPassword) {
        if (httpSession.getAttribute("user") != null) {
            httpSession.removeAttribute("user");
        }
        UserDTO user = userService.getUserByName(userName, RbacConstant.EFFECTIVE_USER);
        String msg = "";
        Map<String, Object> menuVo = new HashMap<>();
        if (user == null || !CommenUtil.md5(userPassword).equals(user.getUserPassword())) {
            msg = "账户不存在或账户";
        }else {
            //hostHolder.setUser(user);
            httpSession.setAttribute("user", user);
            int authorityId = user.getAuthorityId();
            msg = user.getUserName() + "该用户所拥有的菜单:";
            Map<String, Object> menuList = new HashMap<>();
            List<MenuVO> listMenu = menuService.listMenu(authorityId);
            for(MenuVO menuvo : listMenu) {
                menuList.put(menuvo.getMenuName(), menuvo);
            }
            menuVo.put(msg, menuList);
        }
        redisUtil.storeHistory(userName, "user/login", "用户登录");
        return CommenUtil.getJSONString(1, msg, menuVo);
    }

    /**
     *退出登录
     * */
    //@LoginRequired
    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(@RequestParam String userName) {
        httpSession.removeAttribute("user");
        String msg = "";
        if (httpSession.getAttribute("user") == null) {
            msg = "退出登录";
        }else {
            msg = "退出失败";
        }
        return CommenUtil.getJSONString(3, msg);
    }

    /***
     * 用户个人主页
     */
    //@LoginRequired
   // @AuthRequired(AuthCodeEnum.AUTHORITY_USER)
    @RequestMapping(path = "/getUser", method = RequestMethod.POST)
    public String getUserByName(@RequestParam String username) {
        UserDTO userPO = userService.getUserByName(username, RbacConstant.EFFECTIVE_USER);
        Map<String ,Object> userMap = new HashMap<>();
        userMap.put(username, userPO);
        redisUtil.storeHistory(username, "user/getUser", "进入个人主页");
        return CommenUtil.getJSONString(1, "用户主页", userMap);
    }

    /**
     * 查看历史记录
     * */
    //@LoginRequired
    //@AuthRequired(AuthCodeEnum.AUTHORITY_USER)
    @RequestMapping(path = "/getHistory", method = RequestMethod.POST)
    public String getHistoryByName(@RequestParam String userName) {
        //
        Map<String ,Object> history = redisTemplate.opsForHash().entries("userHistory");
        String msg = userName + ":历史记录";
        return CommenUtil.getJSONString(2, msg, history);
    }

    /**
     * 管理员/系统管理人员
     * */
    //@LoginRequired
    //@AuthRequired(AuthCodeEnum.AUTHORITY_FINANCE)
    @RequestMapping(path = "/getUserList", method = RequestMethod.POST)
    public String getUserList() {
        return "ok";
    }

}
