package com.boss.springcloud.service.impl;

import com.boss.springcloud.dao.MenuMapper;
import com.boss.springcloud.entity.po.MenuPO;
import com.boss.springcloud.entity.vo.MenuVO;
import com.boss.springcloud.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;

    /**
     * 查看authorityId对应的所有菜单列表
     * @param authorityId
     * @return
     */
    @Override
    public List<MenuVO> listMenu(int authorityId) {
        return menuMapper.getMenuList(authorityId);
    }
}
