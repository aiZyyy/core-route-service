package com.sixi.core.routeservice.mapper;

import com.sixi.core.routeservice.domain.entity.GatewayRoute;
import com.sixi.core.routeservice.domain.entity.GatewayRouteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created with Mybatis Generator Plugin
 *
 * @author MiaoWoo
 * Created on 2019/10/16 10:26.
 */
@Repository
public interface GatewayRouteMapper {
    long countByExample(GatewayRouteExample example);

    int deleteByExample(GatewayRouteExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(GatewayRoute record);

    int insertSelective(GatewayRoute record);

    List<GatewayRoute> selectByExample(GatewayRouteExample example);

    GatewayRoute selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") GatewayRoute record, @Param("example") GatewayRouteExample example);

    int updateByExample(@Param("record") GatewayRoute record, @Param("example") GatewayRouteExample example);

    int updateByPrimaryKeySelective(GatewayRoute record);

    int updateByPrimaryKey(GatewayRoute record);
}