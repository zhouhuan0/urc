package com.yks.urc.mapper;

import com.yks.urc.entity.UrcWhiteApiVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TODO:
 *
 * @author tangjianbo
 * @date 2019/1/19 10:27
 */
public interface IUrcWhiteApiUrlMapper {

   List<String> selectWhiteApi();

   Integer selectWhiteApiByWiteApi(@Param("whiteApi") String whiteApi);

   int insert(UrcWhiteApiVO iUrcWhiteApiVO);

   int deleteApi(@Param("whiteApi") String whiteApi);
}
