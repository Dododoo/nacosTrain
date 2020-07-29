package com.boss.springcloud.util;


import com.boss.springcloud.entity.dto.UserDTO;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息,用于代替session对象.
 */
@Component
public class HostHolder {

    private ThreadLocal<UserDTO> users = new ThreadLocal<>();

    public void setUser(UserDTO user) {
        users.set(user);
    }

    public UserDTO getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
