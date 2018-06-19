/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/14
 * @since 1.0.0
 */
package com.yks.urc.service;

import com.yks.urc.service.api.IUserService;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.UserVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserserviceTest extends BaseServiceTest {
    @Autowired
   IUserService userService;

    @Test
    public void getuser(){
        UserVO userVO = new UserVO();
        userVO.userName="panyun";
        ResultVO<PageResultVO> resultVO =new ResultVO<>();

        resultVO=userService.getUsersByUserInfo("panyun",userVO,0,10);
       for (int i=0;i<resultVO.data.lst.size();i++){
           System.out.println(resultVO.data.lst.get(i));
       }
    }
    @Test
    public void testMyauthy(){
        ResultVO resultVO = userService.getMyAuthWay("panyun");
        System.out.println(resultVO);
    }
}
