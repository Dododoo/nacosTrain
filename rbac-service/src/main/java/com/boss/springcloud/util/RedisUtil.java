package com.boss.springcloud.util;

import com.boss.springcloud.entity.vo.MenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    //使用redis保存历史记录

    /**
     * 保存用户历史记录到redis
     * @param username
     * @param url
     * @param desc
     */
    public void storeHistory(String username, String url, String desc) {
        //HistoryVO historyVO = new HistoryVO();
        Map<String ,String> history = new HashMap<>();
        Map<String, Object> historyMap = new HashMap<>();
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        df.format(date);
        history.put("createTime", date.toString());
        history.put("url", url);
        history.put("desc", desc);
        historyMap.put(username, history);
        log.info(historyMap.get(username).toString());
        redisTemplate.opsForHash().putAll("userHistory", historyMap);
    }

    /**
     * 将用户对应权限的菜单列表加入到redis中
     * @param menuList
     */
    public void storeMenu(String username, List<MenuVO> menuList) {
        Map<String, Object> menu = new HashMap<>();
        int i = 0;
        for (MenuVO menuVO : menuList) {
            menu.put(menuVO.getMenuUrl(), i);
            i++;
        }
        redisTemplate.opsForHash().putAll(username, menu);
    }
}
