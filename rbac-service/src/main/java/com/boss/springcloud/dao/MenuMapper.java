package com.boss.springcloud.dao;

import com.boss.springcloud.entity.vo.MenuVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MenuMapper {
    List<MenuVO> getMenuList (int authorityId);
}
