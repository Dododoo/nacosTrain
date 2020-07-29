package com.boss.springcloud.dao;

import com.boss.springcloud.entity.dto.UserDTO;
import com.boss.springcloud.entity.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    int addUser(UserPO user);

    List<UserPO> userList(int status);

    UserDTO getUserByName(String userName, int status);

    //删除用户只是将用户的状态为修改为无效状态，而不是真的删除
    int removeUserByName(String userName, int status);
}
