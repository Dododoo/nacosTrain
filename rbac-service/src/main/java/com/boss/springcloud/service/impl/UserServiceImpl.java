package com.boss.springcloud.service.impl;

import com.boss.springcloud.dao.UserMapper;
import com.boss.springcloud.entity.dto.UserDTO;
import com.boss.springcloud.entity.po.UserPO;
import com.boss.springcloud.service.UserService;
import com.boss.springcloud.util.RbacConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Override
    public int addUser(UserPO user) {
        return userMapper.addUser(user);
    }

    /**
     * 删除用户时，根据用户名修改用户的状态
     * @param userName
     * @return
     */
    @Override
    public int removeUserByName(String userName) {
        return userMapper.removeUserByName(userName, RbacConstant.INVALID_USER);
    }

    /**
     * 查看所有状态有效的用户
     * @param status
     * @return
     */
    @Override
    public List<UserPO> userList(int status) {
        return userMapper.userList(status);
    }

    /**
     * 查看状态有效的username用户
     * @param userName
     * @param status
     * @return
     */
    @Override
    public UserDTO getUserByName(String userName, int status) {
        return userMapper.getUserByName(userName, status);
    }
}
