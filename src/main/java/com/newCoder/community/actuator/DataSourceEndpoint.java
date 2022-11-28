package com.newCoder.community.actuator;

import com.newCoder.community.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author lijie
 * @date 2022-11-27 08:25
 * @Desc
 */
@Component
@Endpoint(id = "database") //访问的路径 ----》 "/actuator/database"
@Slf4j
public class DataSourceEndpoint {

    @Autowired
    DataSource dataSource;

    //该方法是get请求  -----> 其他方法可以访问其他请求
    @ReadOperation
    public JsonResult checkDatabase(){
        try (Connection conn = dataSource.getConnection())
        {
            return JsonResult.ok("获取链接成功!");
        } catch (SQLException e) {
            log.error("获取链接失败" + e.getMessage());
            return JsonResult.error("获取链接失败");
        }

    }
}
