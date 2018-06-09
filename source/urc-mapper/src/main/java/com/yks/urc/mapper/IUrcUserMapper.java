/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/7
 * @since 1.0.0
 */
package com.yks.urc.mapper;

import com.yks.urc.entity.UrcUserDo;
import com.yks.urc.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IUrcUserMapper {
    /**
     * 获取域名
     *
     * @param userName
     * @return
     */
    UserVO getUserByName(String userName);

    /**
     * 批量同步到数据库
     *
     * @param userDoList
     * @return
     */
    int insertBatchUser(@Param("userDoList") List<UrcUserDo> userDoList);

    /**
     *  清理数据库,用于同步
     * @return
     */
    int deleteUrcUser();
}
