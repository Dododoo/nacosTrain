package com.boss.springcloud.service;

import com.boss.springcloud.entity.vo.MenuVO;
import java.util.List;

public interface MenuService {
    List<MenuVO> listMenu (int authorityId);
}
