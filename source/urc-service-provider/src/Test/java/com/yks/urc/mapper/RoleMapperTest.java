package com.yks.urc.mapper;

import com.yks.urc.entity.RoleDO;
import com.yks.urc.vo.UserVO;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 〈一句话功能简述〉
 * 角色管理mapper单元测试类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/7 16:12
 * @see RoleMapperTest
 * @since JDK1.8
 */
public class RoleMapperTest extends BaseMapperTest {

    @Autowired
    private IRoleMapper roleMapper;
    

    @Autowired
    private IUserMapper userMapper;
    
    
    @Autowired
    private IUserRoleMapper userRoleMapper;

    @Test
    public void testInsert() {
        RoleDO roleDO = new RoleDO();
        roleDO.setActive(Boolean.TRUE);
        roleDO.setAuthorizable(Boolean.TRUE);
        roleDO.setCreateBy("admin");
        roleDO.setEffectiveTime(new Date());
        roleDO.setCreateTime(new Date());
        roleDO.setForever(Boolean.FALSE);
        roleDO.setRoleName("admin"+System.currentTimeMillis());
        int rtn = roleMapper.insert(roleDO);
        Assert.assertEquals(1, rtn);
    }

    /**
     * 根据roleName做唯一约束，roleName不重复则添加，roleName重复则更新
     * rtn=2表示更新
     * rtn=1表示添加
     */
    @Test
    public void insertOrUpdate() {
        RoleDO roleDO = new RoleDO();
        roleDO.setRoleName("admin121");
        roleDO.setActive(Boolean.TRUE);
        roleDO.setAuthorizable(Boolean.TRUE);
        roleDO.setCreateBy("admin");
        roleDO.setEffectiveTime(new Date());
        roleDO.setCreateTime(new Date());
        roleDO.setForever(Boolean.FALSE);
        int rtn = roleMapper.insertOrUpdate(roleDO);
        System.out.println(rtn);
    }

    @Test
    public void deleteBatch() {
        List<Integer> ids = new ArrayList();
        ids.add(9);
        ids.add(15);
        int rtn = roleMapper.deleteBatch(ids);
        System.out.println(rtn);

    }

    @Test
    public void getRoleByRoleId() {
/*        RoleDO roleDO = roleMapper.getRoleByRoleId(1L);
        Assert.assertNull(roleDO);*/
    	
    	List<UserVO> userList=userMapper.getUserByRoleId("1528856724627000011");

       // List<String> lstUserName= userRoleMapper.getUserNameByRoleId("1528856724627000011");

    }

    @Test
    public void testIsAdminAccount(){
        //非管理员用户
        Assert.assertEquals(roleMapper.isAdminAccount("panyun"), false);
        //管理员用户
        Assert.assertEquals(roleMapper.isAdminAccount("oujie"), true);
    }
    @Test
    public void testGetByRoleName(){
       RoleDO roleDO = roleMapper.getByRoleName("admin");
       Assert.assertNotNull(roleDO);
    }

    @Test
    public void testListRolesByPage(){
        int currPage = 1;
        int pageSize = 3;
        RoleDO roleDO = new RoleDO();
        roleDO.setRoleName("admin");
        roleDO.setRemark("hehe");

        Map<String, Object> map = new HashMap<>();
        map.put("createBy", "admin");
        map.put("roleDO", roleDO);
        map.put("currIndex", (currPage - 1) * pageSize);
        map.put("pageSize", pageSize);

        List<RoleDO> roleDOS = roleMapper.listRolesByPage(map);
        Assert.assertNotNull(roleDOS);
    }
    @Test
    public void getRoleName(){
        String username ="panyun";
       List<String> roleName=  roleMapper.selectRoleNameByUserName(username);
        System.out.println(roleName);
    }
}
