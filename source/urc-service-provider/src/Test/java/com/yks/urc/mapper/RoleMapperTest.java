package com.yks.urc.mapper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.common.util.StringUtil;
import com.yks.urc.entity.DataRuleColDO;
import com.yks.urc.entity.DataRuleTemplDO;
import com.yks.urc.entity.ExpressionDO;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.RolePermissionDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.vo.DataRuleColVO;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.DataRuleVO;
import com.yks.urc.vo.ExpressionVO;
import com.yks.urc.vo.UserVO;
import com.yks.urc.vo.helper.VoHelper;

import org.drools.compiler.lang.DRL5Expressions.literal_return;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
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
	IRolePermissionMapper rolePermissionMapper;

    @Autowired
    private IRoleMapper roleMapper;
    
    
    @Autowired
    private IDataRuleTemplMapper dataRuleTemplMapper;
    
    @Autowired
    private IExpressionMapper expressionMapper;
    
    @Autowired
    private IDataRuleColMapper dataRuleColMapper;
    
    
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
    	
		List<RolePermissionDO> rolePermissionList=rolePermissionMapper.getUserAuthorizablePermission("panyun");
    	
    	
    	
/*    	List<String> lstUserName=new ArrayList<String>();
    	lstUserName.add("panyun");
    	List<DataRuleVO> dataRuel=new ArrayList<DataRuleVO>();
		for (int i = 0; i < lstUserName.size(); i++) {
			//通过用户名得到sys_key
			List<String> syskeyList=userRoleMapper.getSysKeyByUser(lstUserName.get(i));
			List<DataRuleSysVO> lstDataRuleSys =new ArrayList<DataRuleSysVO>();
			for (int j = 0; j < syskeyList.size(); j++) {
				//通过sysKey得到 行权限
				List<ExpressionDO> expressionList= expressionMapper.listExpressionDOsBySysKey(syskeyList.get(i));
				//通过sysKey得到列权限
				List<DataRuleColDO> dataRuleColList =dataRuleColMapper.listRuleColBySysKey(syskeyList.get(i));
				List<DataRuleColVO> dataRuleColVOList =new ArrayList<DataRuleColVO>();
				 for (DataRuleColDO colDO : dataRuleColList) {
					DataRuleColVO dataRuleColVO=new DataRuleColVO();
					 BeanUtils.copyProperties(colDO, dataRuleColVO);
					 dataRuleColVOList.add(dataRuleColVO);
				}
				List<ExpressionVO> expressionVOList =new ArrayList<ExpressionVO>();
				 for (ExpressionDO  expressionDO : expressionList) {
					 ExpressionVO expressionVO=new ExpressionVO();
					 BeanUtils.copyProperties(expressionDO, expressionVO);
					 expressionVOList.add(expressionVO);
				}
				DataRuleSysVO dataRuleSysVO=new DataRuleSysVO();	
				ExpressionVO expressionVO=new ExpressionVO();
				expressionVO.setSubWhereClause(expressionVOList);
				dataRuleSysVO.sysKey=syskeyList.get(i);
				dataRuleSysVO.col=dataRuleColVOList;
				dataRuleSysVO.row=expressionVO;
				lstDataRuleSys.add(dataRuleSysVO);
			}
			DataRuleVO dataRuleVO=new DataRuleVO();
			dataRuleVO.userName=lstUserName.get(i);
			dataRuleVO.lstDataRuleSys=lstDataRuleSys;
			dataRuel.add(dataRuleVO);
		}
		
		System.out.println(StringUtility.toJSONString_NoException(dataRuel));*/
		
/*        RoleDO roleDO = roleMapper.getRoleByRoleId(1L);
        Assert.assertNull(roleDO);*/
		//List<DataRuleTemplDO> dataRuleTempList=dataRuleTemplMapper.getMyDataRuleTempl("admin");
/*    	List<String> syskeyList=new ArrayList<String>();
    	syskeyList.add("1");
    	syskeyList.add("2");
		List<ExpressionDO> expressionList= expressionMapper.listExpressionDOsBySysKey(syskeyList);*/
		
    	
/*    	List<String> syskeyList=new ArrayList<String>();
    	syskeyList.add("1");
    	syskeyList.add("2");
		List<DataRuleColDO> dataRuleColList =dataRuleColMapper.listRuleColBySysKey(syskeyList);*/
		
    	//List<DataRuleColDO> dataRuleColList =dataRuleColMapper.listRuleColBySysKey("001");

    	//List<UserVO> userList=userMapper.getUserByRoleId("1528856724627000011");

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
}
