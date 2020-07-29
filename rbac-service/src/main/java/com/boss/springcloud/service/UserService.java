package com.boss.springcloud.service;

import com.boss.springcloud.entity.dto.UserDTO;
import com.boss.springcloud.entity.po.UserPO;
import java.util.List;

public interface UserService {
    int addUser(UserPO user);

    int removeUserByName(String userName);

    List<UserPO> userList(int status);

    UserDTO getUserByName(String userName, int status);

}
