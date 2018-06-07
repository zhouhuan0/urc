package com.yks.urc.dingding.client;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yks.urc.dingding.client.vo.DingApiRespVO;
import com.yks.urc.dingding.client.vo.DingDeptVO;
import com.yks.urc.dingding.client.vo.DingUserVO;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;

/**
 * 获取钉钉部门以及成员信息
 * @Author: wujianghui@youkeshu.com
 * @Date: 2018/6/6 8:59
 */
public class DingApiProxyImpl implements  DingApiProxy{
	
	private static Logger LOG = LoggerFactory.getLogger(DingApiProxyImpl.class);
	
	/**
	 * 获取钉钉accessToken
	 * @return
	 * @throws Exception
	 */
    public  String getDingAccessToken() throws Exception {
        	String accToken = "";
        	String accessTokenUrl = "https://oapi.dingtalk.com";
            try {
            	StringBuffer param=new StringBuffer();
            	String corpid="dinge8d7141acdb006a135c2f4657eb6378f";
            	String corpsecret="1Tf9YqLFKPNF0xJumHQWmZYGt9HdpPjlWT68P1NJu3yWYM1r9hJAajlFbXaZeuis";
            	param.append("corpid=").append(corpid).append("&corpsecret=").append(corpsecret);
            	
            	LOG.info("调用钉钉获取accessTokenc参数,parama={}",param.toString());
            	
            	if(accessTokenUrl.endsWith("/")){
            		accessTokenUrl=accessTokenUrl.substring(0, accessTokenUrl.length()-1);
            	}
            	accessTokenUrl+="/gettoken";
            	String strResp= HttpUtility.sendGet(accessTokenUrl, param.toString());
            	LOG.info("钉钉accessTokenc响应,strResp={}",strResp);
        		DingApiRespVO resp = StringUtility.parseObject(strResp, DingApiRespVO.class);
        		if (resp != null) {
        			if (resp.errcode == 0) {
        				accToken=resp.access_token;
        			}
        		}
            } catch (Exception e) {
            	LOG.info("钉钉获取accessToken出错，message={}",e.getMessage());
    			throw new Exception(e.getMessage());
            }

        return accToken;
    }
    
    /**
     * 得到所有的部门
     * @return
     * @throws Exception
     */
    public  List<DingDeptVO> getDingAllDept() throws Exception {
    	List<DingDeptVO> department = null;
    	String accessTokenUrl = "https://oapi.dingtalk.com";
    	 try {
    		String accToken = getDingAccessToken();
         	StringBuffer param=new StringBuffer();
         	param.append("access_token=").append(accToken).append("&fetch_child=").append("true");
         	LOG.info("调用钉钉获取所有部门信息参数,parama={}",param.toString());
         	department=getDingInfo(department, accessTokenUrl, param.toString());
         } catch (Exception e) {
         	LOG.info("钉钉获取所有部门出错，message={}",e.getMessage());
 			throw new Exception(e.getMessage());
         }
        return department;
    }
    
    /**
     * 得到部门下的直接子部门
     * @param departmentId
     * @return
     * @throws Exception
     */
    public  List<DingDeptVO> getDingSubDept(String departmentId) throws Exception {
    	List<DingDeptVO> department = null;
    	String accessTokenUrl = "https://oapi.dingtalk.com";
    	 try {
    		String accToken = getDingAccessToken();
         	StringBuffer param=new StringBuffer();
         	param.append("access_token=").append(accToken).append("&id=").append(departmentId);
         	LOG.info("调用钉钉获取所有部门信息参数,departmentId={},parama={}",departmentId,param.toString());
         	department=getDingInfo(department, accessTokenUrl, param.toString());
         } catch (Exception e) {
         	LOG.info("钉钉获取所有部门出错，message={}",e.getMessage());
 			throw new Exception(e.getMessage());
         }
        return department;
    }
    
    /**
     * 得到部门下的所有部门
     * @param departmentId
     * @return
     * @throws Exception
     */
    public  List<DingDeptVO> getDingAllSubDept(String departmentId) throws Exception {
    	List<DingDeptVO> department = null;
    	String accessTokenUrl = "https://oapi.dingtalk.com";
    	 try {
    		String accToken = getDingAccessToken();
         	StringBuffer param=new StringBuffer();
         	param.append("access_token=").append(accToken).append("&fetch_child").append("true").append("&id=").append(departmentId);
         	LOG.info("调用钉钉获取所有子部门信息参数,departmentId={},parama={}",departmentId,param.toString());
         	department=getDingInfo(department, accessTokenUrl, param.toString());
         } catch (Exception e) {
         	LOG.info("钉钉获取所有部门出错，message={}",e.getMessage());
 			throw new Exception(e.getMessage());
         }
        return department;
    }
    
    
    private   List<DingDeptVO> getDingInfo(List<DingDeptVO> department,String accessTokenUrl,String param) throws Exception{
     	String strResp;
		try {
         	if(accessTokenUrl.endsWith("/")){
         		accessTokenUrl=accessTokenUrl.substring(0, accessTokenUrl.length()-1);
         	}
         	accessTokenUrl+="/department/list";
			strResp = HttpUtility.sendGet(accessTokenUrl, param);
			LOG.info("钉钉所有部门响应,strResp={}",strResp);
			DingApiRespVO resp = StringUtility.parseObject(strResp, DingApiRespVO.class);
			if (resp != null && resp.errcode == 0) {
				department=resp.department;
			}
		} catch (Exception e) {
         	LOG.info("钉钉获取所有部门出错，message={}",e.getMessage());
 			throw new Exception(e.getMessage());
		}
		return department;
    }
    
    
    /**
     * 获取钉钉部门下的成员信息
     * @param departmentId 部门id
     * @return 
     * @throws Exception
     */
    public  List<DingUserVO> getDingMemberByDepId(String departmentId) throws Exception {
    	List<DingUserVO> userlist = null;
    	String accessTokenUrl = "https://oapi.dingtalk.com";
    	String KEY_BIRTHDAY = "生日";
    	String KEY_GENDER = "性别";
    	 try {
    		String accToken = getDingAccessToken();
         	if(accessTokenUrl.endsWith("/")){
         		accessTokenUrl=accessTokenUrl.substring(0, accessTokenUrl.length()-1);
         	}
         	
         	accessTokenUrl+="/user/list";
         	StringBuffer param=new StringBuffer();
         	param.append("access_token=").append(accToken).append("&department_id=").append(departmentId).append("&order=").append("entry_desc");
         	LOG.info("调用钉钉获取部门下成员信息参数,departmentId={},parama={}",departmentId,param.toString());
         	String strResp= HttpUtility.sendGet(accessTokenUrl, param.toString());
         	LOG.info("钉钉部门下成员响应,departmentId={},strResp={}",departmentId,strResp);
     		DingApiRespVO resp = StringUtility.parseObject(strResp, DingApiRespVO.class);
			if (resp != null && resp.errcode == 0 && resp.userlist != null && resp.userlist.size() > 0) {
				for (DingUserVO u : resp.userlist) {
					u.joinDate = new Date(u.hiredDate);
					if (u.extattr != null) {
						// 生日、性别在扩展字段中
						if (u.extattr.containsKey(KEY_BIRTHDAY)) {
							u.birthday = StringUtility.convertToDate(u.extattr.get(KEY_BIRTHDAY), null);
						}
						if (u.extattr.containsKey(KEY_GENDER)) {
							u.gender = u.extattr.get(KEY_GENDER);
						}
					}
				}
				userlist=resp.userlist;
			}
         } catch (Exception e) {
         	LOG.info("钉钉获取部门下的成员出错，message={}",e.getMessage());
 			throw new Exception(e.getMessage());
         }
        return userlist;
    }
    
    

}
