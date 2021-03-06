package com.yks.urc.userValidate.bp.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.fw.StringUtil;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.PermissionDO;
import com.yks.urc.entity.UrcWhiteApiVO;
import com.yks.urc.entity.UserLoginLogDO;
import com.yks.urc.entity.UserPermitStatDO;
import com.yks.urc.entity.UserTicketDO;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUrcWhiteApiUrlMapper;
import com.yks.urc.mapper.PermissionMapper;
import com.yks.urc.mapper.UserTicketMapper;
import com.yks.urc.operation.bp.api.IOperationBp;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.user.bp.api.IUserLogBp;
import com.yks.urc.userValidate.bp.api.ITicketUpdateBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.FunctionVO;
import com.yks.urc.vo.GetAllFuncPermitRespVO;
import com.yks.urc.vo.MenuVO;
import com.yks.urc.vo.ModuleVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.SystemRootVO;
import com.yks.urc.vo.UserVO;
import com.yks.urc.vo.helper.VoHelper;

@Component
public class UserValidateBp implements IUserValidateBp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IRoleMapper roleMapper;
    @Autowired
    ICacheBp cacheBp;
    @Autowired
    private IUrcWhiteApiUrlMapper urcWhiteApiUrlMapper;

    @Autowired
    IOperationBp operationBp;
    @Autowired
    private ISessionBp sessionBp;

    @Override
    public List<String> getFuncJsonLstByUserAndSysKey(String userName, String sysKey) {
        return roleMapper.getFuncJsonByUserAndSysKey(userName, sysKey);
    }

    /**
     * ??????funcVersion
     *
     * @param strFuncJson
     * @return
     * @author panyun@youkeshu.com
     * @date 2018???6???12??? ??????3:56:19
     */
    @Override
    public String calcFuncVersion(String strFuncJson) {
        return StringUtility.md5_NoException(strFuncJson);
    }

    /**
     * ????????????module
     *
     * @param sys1
     * @param userName
     * @author panyun@youkeshu.com
     * @date 2018???6???12??? ??????7:22:38
     */
    @Override
    public List<UserPermitStatDO> plainSys(SystemRootVO sys1, String userName) {
        if (sys1 == null) return Collections.emptyList();
        List<MenuVO> lstMenu = sys1.menu;

        List<ModuleVO> lstModuleRslt = new ArrayList<>();
        for (MenuVO menu : lstMenu) {
            List<ModuleVO> lstModule = menu.module;
            if (lstModule == null)
                continue;
            for (ModuleVO m : lstModule) {
                m.pageFullPathName.append(m.name);
                m.lstChildFunc = getChildrenFuncDesc(m);
                m.sysKey = sys1.system.key;
                lstModuleRslt.add(m);
                plainModule(m, lstModuleRslt);
            }
        }

        List<UserPermitStatDO> lstRslt = new ArrayList<>();
        for (ModuleVO m : lstModuleRslt) {
            UserPermitStatDO statDo = new UserPermitStatDO();
            statDo.setUserName(userName);
            statDo.setSysKey(sys1.system.key);
            statDo.setModuleName(m.pageFullPathName.toString());
            statDo.setFuncJson(StringUtility.toJSONString_NoException(m.lstChildFunc));
            // System.out.println(m.sysKey + " " + m.pageFullPathName + " " +
            // StringUtility.toJSONString_NoException(m.lstChildFunc));
            lstRslt.add(statDo);
        }

        return lstRslt;
    }

    @Override
    public List<ModuleVO> plainModule(SystemRootVO sys1) {
        if (sys1 == null) return Collections.emptyList();
        List<MenuVO> lstMenu = sys1.menu;

        List<ModuleVO> lstModuleRslt = new ArrayList<>();
        for (MenuVO menu : lstMenu) {
            List<ModuleVO> lstModule = menu.module;
            if (lstModule == null)
                continue;
            for (ModuleVO m : lstModule) {
                m.pageFullPathName.append(m.name);
                m.lstChildFunc = getChildrenFuncDesc(m);
                m.sysKey = sys1.system.key;
                lstModuleRslt.add(m);
                plainModule(m, lstModuleRslt);
            }
        }
        return lstModuleRslt;
    }

    /**
     * ???module????????????
     *
     * @param m
     * @param lstModuleRslt
     * @author panyun@youkeshu.com
     * @date 2018???6???12??? ??????7:21:54
     */
    private void plainModule(ModuleVO m, List<ModuleVO> lstModuleRslt) {
        if (m.module != null) {
            for (ModuleVO mem : m.module) {
                // if (mem.pageFullPathName == null)
                // mem.pageFullPathName = new StringBuilder();
                mem.pageFullPathName.append(m.pageFullPathName);
                mem.pageFullPathName.append("/");
                mem.pageFullPathName.append(mem.name);
                mem.lstChildFunc = getChildrenFuncDesc(mem);
                mem.sysKey = m.sysKey;
                lstModuleRslt.add(mem);
                plainModule(mem, lstModuleRslt);
            }
        }
    }

    /**
     * ??????module????????????function name
     *
     * @param mem
     * @return
     * @author panyun@youkeshu.com
     * @date 2018???6???12??? ??????7:09:28
     */
    private List<String> getChildrenFuncDesc(ModuleVO mem) {
        if (mem == null || mem.function == null || mem.function.size() == 0)
            return null;
        List<String> sbFunc = new ArrayList();
        for (FunctionVO f : mem.function) {
            calcFuncDesc(f, sbFunc);
        }
        return sbFunc;
    }

    /**
     * ??????????????????function name
     *
     * @param f
     * @param sbRslt
     * @author panyun@youkeshu.com
     * @date 2018???6???12??? ??????7:09:00
     */
    private void calcFuncDesc(FunctionVO f, List<String> sbRslt) {
        sbRslt.add(f.name);
        if (f.function != null) {
            for (FunctionVO fMem : f.function) {
                calcFuncDesc(fMem, sbRslt);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // ??????func1.json??????
        String strJson1 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("func1.json"));
        String strJson2 = StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("func2.json"));
        System.out.println(new UserValidateBp().cleanDeletedNode(strJson1, strJson2));

        // SystemRootVO sys1 = StringUtility.parseObject(strJson1, SystemRootVO.class);
        // System.out.println(StringUtility.toJSONString_NoException(sys1));
        // new UserValidateBp().plainSys(sys1, "panyun");

        // ??????func2.json??????
        // String strJson2 =
        // StringUtility.inputStream2String(ClassLoader.getSystemResourceAsStream("func2.json"));
        // SystemRootVO sys2 = StringUtility.parseObject(strJson2, SystemRootVO.class);
        //
        // System.out.println("????????????sys1:" +
        // StringUtility.toJSONString_NoException(sys1));
        // // sys2???????????????????????????sys1???
        // distinctSystemRootVO(sys1, sys2);
        // System.out.println("????????????sys1:" +
        // StringUtility.toJSONString_NoException(sys1));
    }

    /**
     * ????????????json????????????old???newest???????????????old????????????newest????????????node??????
     *
     * @param strFuncJsonOld
     * @param strFuncJsonNewest
     * @return
     * @author panyun@youkeshu.com
     * @date 2018???6???15??? ??????8:26:43
     */
    @Override
    public String cleanDeletedNode(String strFuncJsonOld, String strFuncJsonNewest) {
        SystemRootVO sysOld = StringUtility.parseObject(strFuncJsonOld, SystemRootVO.class);
        SystemRootVO sysNewest = StringUtility.parseObject(strFuncJsonNewest, SystemRootVO.class);

        // ???newest????????????key??????List
        List<String> lstNewestKey = new ArrayList<>();
        if (sysNewest.menu != null) {
            for (MenuVO m : sysNewest.menu) {
                lstNewestKey.add(m.key);
                searchModuleKey(m.module, lstNewestKey);
            }
        }

        // ???old??????key???List????????????????????????
        // ?????? menu????????????,??????????????????
        if (sysOld.menu != null) {
            for (int i = 0; i < sysOld.menu.size(); i++) {
                if (lstNewestKey.contains(sysOld.menu.get(i).key))
                    continue;
                // ??????menu
                sysOld.menu.remove(i);
                i--;
            }
        }

        if (!CollectionUtils.isEmpty(sysOld.menu)) {
            for (MenuVO m : sysOld.menu) {
                cleanDeletedModule(m.module, lstNewestKey);
            }
        }

        return StringUtility.toJSONString_NoException(sysOld);
    }

    /**
     * ????????????module??????????????????node
     *
     * @param lstModule
     * @param lstNewestKey
     * @author panyun@youkeshu.com
     * @date 2018???6???15??? ??????8:53:44
     */
    private void cleanDeletedModule(List<ModuleVO> lstModule, List<String> lstNewestKey) {
        if (lstModule == null || lstNewestKey == null)
            return;
        for (int i = 0; i < lstModule.size(); i++) {
            if (lstNewestKey.contains(lstModule.get(i).key)) {
                // ????????????module??????????????????node
                cleanDeletedModule(lstModule.get(i), lstNewestKey);
                continue;
            }
            // ??????module
            lstModule.remove(i);
            i--;
        }
    }

    /**
     * ????????????module??????????????????node
     *
     * @param moduleVO
     * @param lstNewestKey
     * @author panyun@youkeshu.com
     * @date 2018???6???15??? ??????8:46:27
     */
    private void cleanDeletedModule(ModuleVO moduleVO, List<String> lstNewestKey) {
        if (moduleVO == null || lstNewestKey == null)
            return;

        cleanDeletedModule(moduleVO.module, lstNewestKey);
        cleanDeletedFunction(moduleVO.function, lstNewestKey);
    }

    /**
     * ????????????function??????????????????node
     *
     * @param lstFunc
     * @param lstNewestKey
     * @author panyun@youkeshu.com
     * @date 2018???6???15??? ??????8:53:52
     */
    private void cleanDeletedFunction(List<FunctionVO> lstFunc, List<String> lstNewestKey) {
        if (lstFunc == null || lstNewestKey == null)
            return;
        for (int i = 0; i < lstFunc.size(); i++) {
            if (lstNewestKey.contains(lstFunc.get(i).key)) {
                cleanDeletedFunction(lstFunc.get(i).function, lstNewestKey);
                continue;
            }
            lstFunc.remove(i);
            i--;
        }
    }

    /**
     * ????????????module???key
     *
     * @param lstModule
     * @param lstNewestKey
     * @author panyun@youkeshu.com
     * @date 2018???6???15??? ??????8:37:41
     */
    private void searchModuleKey(List<ModuleVO> lstModule, List<String> lstNewestKey) {
        if (lstModule != null) {
            for (ModuleVO module : lstModule) {
                lstNewestKey.add(module.key);
                searchModuleKey(module.module, lstNewestKey);
                searchFunctionKey(module.function, lstNewestKey);
            }
        }
    }

    /**
     * ????????????function???key
     *
     * @param lstFunc
     * @param lstNewestKey
     * @author panyun@youkeshu.com
     * @date 2018???6???15??? ??????8:37:28
     */
    private void searchFunctionKey(List<FunctionVO> lstFunc, List<String> lstNewestKey) {
        if (lstFunc == null)
            return;
        for (FunctionVO f : lstFunc) {
            lstNewestKey.add(f.key);
            if (f.function != null) {
                searchFunctionKey(f.function, lstNewestKey);
            }
        }
    }

    /**
     * sys2???????????????????????????sys1???
     *
     * @param sys1
     * @param sys2
     * @author panyun@youkeshu.com
     * @date 2018???5???26??? ??????11:51:59
     */
    private static void distinctSystemRootVO(SystemRootVO sys1, SystemRootVO sys2) {
        if (sys1.menu == null)
            sys1.menu = new ArrayList<>();
        if (sys2.menu == null)
            sys2.menu = new ArrayList<>();
        List<MenuVO> menu1 = sys1.menu;
        List<MenuVO> menu2 = sys2.menu;

        for (int j = 0; j < menu2.size(); j++) {
            boolean hasSameMenu = false;
            for (int i = 0; i < menu1.size(); i++) {
                if (StringUtility.stringEqualsIgnoreCase(menu1.get(i).key, menu2.get(j).key)) {
                    // ????????????menu
                    distinctMenu(menu1.get(i), menu2.get(j));
                    hasSameMenu = true;
                    break;
                }
            }

            if (hasSameMenu) {
                // ?????????menu,??????????????????menu??????,menu2??????????????????
                menu2.remove(j);
                j--;
            } else {
                // ????????????menu,menu2???????????????menu1???????????????menu2???????????????
                menu1.add(menu2.get(j));
                menu2.remove(j);
                j--;
            }
        }
    }

    private static void distinctMenu(MenuVO menu1, MenuVO menu2) {
        // ??????menu??????page
        if (menu1.module == null)
            menu1.module = new ArrayList<>();
        if (menu2.module == null)
            menu2.module = new ArrayList<>();

        List<ModuleVO> page1 = menu1.module;
        List<ModuleVO> page2 = menu2.module;
        distinctPages(page1, page2);
    }

    private static void distinctPages(List<ModuleVO> page1, List<ModuleVO> page2) {
        if (page1 == null)
            page1 = new ArrayList<>();
        if (page2 == null)
            page2 = new ArrayList<>();
        for (int j = 0; j < page2.size(); j++) {
            boolean hasSameMenu = false;
            for (int i = 0; i < page1.size(); i++) {
                if (StringUtility.stringEqualsIgnoreCase(page1.get(i).key, page2.get(j).key)) {
                    // ????????????page
                    distinctPage(page1.get(i), page2.get(j));
                    hasSameMenu = true;
                    break;
                }
            }

            if (hasSameMenu) {
                // ?????????page,??????????????????page??????,page2??????????????????
                page2.remove(j);
                j--;
            } else {
                // ????????????page,page2???????????????page1???????????????page2???????????????
                page1.add(page2.get(j));
                page2.remove(j);
                j--;
            }
        }
    }

    private static void distinctPage(ModuleVO page1, ModuleVO page2) {
        // ??????page??????pages
        distinctPages(page1.module, page2.module);
        // ??????page??????functions
        distinctFunctions(page1.function, page2.function);
    }

    private static void distinctFunctions(List<FunctionVO> functions1, List<FunctionVO> functions2) {
        if (functions1 == null)
            functions1 = new ArrayList<>();
        if (functions2 == null)
            functions2 = new ArrayList<>();
        for (int j = 0; j < functions2.size(); j++) {
            boolean hasSameMenu = false;
            for (int i = 0; i < functions1.size(); i++) {
                if (StringUtility.stringEqualsIgnoreCase(functions1.get(i).key, functions2.get(j).key)) {
                    // ????????????functions
                    distinctFunction(functions1.get(i), functions2.get(j));
                    hasSameMenu = true;
                    break;
                }
            }

            if (hasSameMenu) {
                // ?????????page,??????????????????page??????,page2??????????????????
                functions2.remove(j);
                j--;
            } else {
                // ????????????page,page2???????????????page1???????????????page2???????????????
                functions1.add(functions2.get(j));
                functions2.remove(j);
                j--;
            }
        }
    }

    private static void distinctFunction(FunctionVO function1, FunctionVO function2) {
        // ??????function??????functions
        distinctFunctions(function1.function, function2.function);
    }

    @Override
    public String createTicket(String strUserName) {
        return StringUtility.md5_NoException(String.format("%s%s", strUserName, StringUtility.getUUIDLowercase_Dt()));

    }

    @Override
    public String getFuncJsonByUserAndSysKey(String userName, String sysKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String mergeFuncJson(List<String> lstJson) {
        return StringUtility.toJSONString_NoException(mergeFuncJson2Obj(lstJson));
    }

    @Override
    public SystemRootVO mergeFuncJson2Obj(List<String> lstJson) {
        if (lstJson == null || lstJson.size() == 0)
            return null;
        List<SystemRootVO> lstSysRootVO = new ArrayList<>();
        for (String mem : lstJson) {
            SystemRootVO sys1 = StringUtility.parseObject(mem, SystemRootVO.class);
            if (sys1 != null) lstSysRootVO.add(sys1);
        }
        if (lstSysRootVO.size() == 0) return null;
        SystemRootVO sys1 = lstSysRootVO.get(0);
        for (int i = 1; i < lstSysRootVO.size(); i++) {
            // sys2???????????????????????????sys1???
            SystemRootVO sys2 = lstSysRootVO.get(i);
            distinctSystemRootVO(sys1, sys2);
        }
        return checkSystemRootVo(sys1);
    }

   /* private List<String> lstWhiteApiUrl = Arrays.asList("/urc/motan/service/api/IUrcService/getAllFuncPermit",
            "/urc/motan/service/api/IUrcService/logout");*/

    private SystemRootVO checkSystemRootVo(SystemRootVO sys1) {
    	if(null == sys1) return null;
    	if(CollectionUtils.isEmpty(sys1.menu)) return null;
    	
    	Iterator<MenuVO> it = sys1.menu.iterator();
    	while (it.hasNext()) {
    		if(null == checkoutMenuVO(it.next())){
    			it.remove();
    		}
    	}
    	return sys1;
		
	}

	private MenuVO checkoutMenuVO(MenuVO menuVO) {
		
		if(null == menuVO || CollectionUtils.isEmpty(menuVO.module)) return null;
		
		return menuVO;
		
	}

	@Autowired
    private ISerializeBp serializeBp;

	public List<String> lstWhiteApiUrl() {
        List<String> whiteApi = new ArrayList<>();
        String whiteApiCash = cacheBp.getWhiteApi("api");
        if (whiteApiCash == null) {
            whiteApi = urcWhiteApiUrlMapper.selectWhiteApi();
            if (whiteApi == null) {
                whiteApi = new ArrayList<>();
            }
            String apiStr = serializeBp.obj2Json(whiteApi);
            //??????api
            cacheBp.insertWhiteApi(apiStr);
        } else {
            whiteApi = serializeBp.json2ObjNew(whiteApiCash, new TypeReference<List<String>>() {
            });
        }
        return whiteApi;

    }


    @Autowired
    private IUserLogBp userLogBp;
    @Autowired
    private UserTicketMapper userTicketMapper;

    @Autowired
    private ITicketUpdateBp ticketUpdateBp;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

    @Override
    public ResultVO funcPermitValidate(Map<String, String> map) {
        logger.info(String.format("funcPermitValidate_request:%s", StringUtility.toJSONString_NoException(map)));
        try {
            String apiUrl = map.get("apiUrl");
            String moduleUrl = map.get("moduleUrl");
            String operator = map.get(StringConstant.operator);
            if (StringUtility.isNullOrEmpty(operator)) {
                return VoHelper.getResultVO("100002", "????????????:operator????????????");
            }
            String ticket = map.get(StringConstant.ticket);
            String urcVersion = map.get(StringConstant.funcVersion);
            String deviceType = map.get(StringConstant.deviceType);
            UserVO u = cacheBp.getUser(operator,deviceType);
            // ??????ticket
            UserLoginLogDO loginLogDO = new UserLoginLogDO();
            loginLogDO.userName = operator;
            loginLogDO.createTime = new Date();
            loginLogDO.modifiedTime = new Date();
            UserTicketDO userTicketDO;
            if (u == null) {
                //??????????????????????????????null ???????????????????????????ticket??????
                userTicketDO = userTicketMapper.selectUserTicketByUserName(operator,deviceType);
                if (StringUtil.isEmpty(userTicketDO)) {
                    //??????????????????????????????ticket??????
                    loginLogDO.remark = String.format("funcPermitValidate,request:[%s],?????????ticket:[%s];(?????????????????????)", StringUtility.toJSONString(map), ticket);
                    userLogBp.insertLog(loginLogDO);
                    logger.error(String.format("funcPermitValidate login timeout request = %s", StringUtility.toJSONString(map)));
                    return VoHelper.getResultVO("100002", "????????????:??????????????????");
                }
                //????????????
                ResultVO deviceChange = checkDeviceFromDB(userTicketDO, loginLogDO, ticket,StringUtility.obj2Json(map));
                if (deviceChange != null) {
                    return deviceChange;
                }
                Date now = new Date();
                //??????ticket?????????????????????null ???????????????
                if (now.before(userTicketDO.getExpiredTime())) {
                    UserVO userVO = new UserVO();
                    userVO.ticket = userTicketDO.getTicket();
                    userVO.userName = userTicketDO.getUserName();
                    userVO.loginTime = userTicketDO.getModifiedTime().getTime();
                    userVO.ip = map.get(StringConstant.ip);
                    userVO.deviceName = map.get(StringConstant.deviceName);
                    userVO.deviceType = deviceType;
                    cacheBp.insertUser(userVO);
                }
                //??????????????????ExpiredTime?????????ticket????????????
                if (now.after(userTicketDO.getExpiredTime())) {
                    // 100002
                    loginLogDO.remark = String.format("funcPermitValidate ,request:[%s],?????????ticket:[%s];(????????????????????????ticket??????:[%s])", StringUtility.toJSONString(map), ticket, StringUtility.toJSONString(userTicketDO.getTicket()));
                    userLogBp.insertLog(loginLogDO);
                    logger.error(String.format("funcPermitValidate login timeout request = %s ,ticket =%s;(????????????????????????ticket??????:[%s])", StringUtility.toJSONString(map), ticket, StringUtility.toJSONString(userTicketDO.getTicket())));
                    return VoHelper.getResultVO("100002", "????????????:ticket?????????");
                }

            } else {
                //????????????
                ResultVO deviceChange = checkDeviceFromCache(u, loginLogDO, ticket,StringUtility.obj2Json(map));
                if (deviceChange != null) {
                    return deviceChange;
                }
            }
            // ???????????????ticket????????????
            ticketUpdateBp.refreshExpiredTime(operator,deviceType, ticket);

            if (this.lstWhiteApiUrl().contains(apiUrl)) {
                return VoHelper.getResultVO(StringConstant.STATE_100006, "??????????????????????????????(??????api????????????)");
            }

            // ??????????????????????????????

            // ?????????????????????
            String sysKey = getSysKeyByApiUrl(apiUrl);
            if (StringUtility.isNullOrEmpty(sysKey)) {
                return VoHelper.getResultVO(ErrorCode.E_100007, String.format("%s don't belong to any system", apiUrl));
            }
            if (!hasApiFunc(moduleUrl, apiUrl, operator, sysKey)) {
                return VoHelper.getResultVO(ErrorCode.E_100003, "????????????");
            }
            return VoHelper.getResultVO(StringConstant.STATE_100006, "??????????????????????????????");
        } catch (Exception e) {
            logger.error("??????????????????,?????????", e);
            throw new URCBizException(ErrorCode.E_000008.getState(), "??????????????????");
        }
    }

    @Nullable
    private ResultVO checkDeviceFromCache(UserVO uFromCache, UserLoginLogDO loginLogDO, String ticketFromRequest,String requestParam) {

        if (!StringUtility.stringEqualsIgnoreCase(uFromCache.ticket, ticketFromRequest)) {
            loginLogDO.remark = String.format("request's ticket not same as cache's ticket,request:%s cache:%s",
                    requestParam,
                    StringUtility.obj2Json(uFromCache));
            userLogBp.insertLog(loginLogDO);
            String loginTimeString = "";
            if (uFromCache.loginTime != null) {
                loginTimeString = simpleDateFormat.format(uFromCache.loginTime);
            }
//            logger.info(String.format("Your account has been successfully logged in to another device (%s;IP:%s) at :%s. Please log in again and check whether your account password has been leaked, and modify the password in time???", uFromCache.deviceName, uFromCache.ip, loginTimeString));
            return VoHelper.getResultVO("101003", String.format("???????????????%s ??????????????????%s;IP:%s????????????????????????????????????????????????????????????????????????????????????", loginTimeString, uFromCache.deviceName, uFromCache.ip));
        }
        return null;
    }

    @Nullable
    private ResultVO checkDeviceFromDB(UserTicketDO uFromDb, UserLoginLogDO loginLogDO, String ticketFromRequest,String requestParam) {

        if (!StringUtility.stringEqualsIgnoreCase(uFromDb.getTicket(), ticketFromRequest)) {
            loginLogDO.remark = String.format("request's ticket not same as db's ticket,request:%s cache:%s",
                    requestParam,
                    StringUtility.obj2Json(uFromDb));
//            loginLogDO.remark = String.format("???????????????%s ??????????????????%s;IP:%s????????????????????????????????????????????????????????????????????????????????????????????????", loginTimeString, uFromDb.getDeviceName(), uFromDb.getLoginIp());
            userLogBp.insertLog(loginLogDO);
            String loginTimeString="";
            if (uFromDb.getLoginTime() != null) {
                loginTimeString = simpleDateFormat.format(uFromDb.getLoginTime());
            }
//            logger.info(String.format("Your account has been successfully logged in to another device (%s;IP:%s) at :%s. Please log in again and check whether your account password has been leaked, and modify the password in time???", uFromDb.getDeviceName(), uFromDb.getLoginIp(), loginTimeString));
            return VoHelper.getResultVO("101003", String.format("???????????????%s ??????????????????%s;IP:%s????????????????????????????????????????????????????????????????????????????????????", loginTimeString, uFromDb.getDeviceName(), uFromDb.getLoginIp()));
        }
        return null;
    }

    /**
     * ??????apiUrl?????????sysKey
     *
     * @param strApiUrl
     * @return
     * @author panyun@youkeshu.com
     * @date 2018???6???25??? ??????2:30:19
     */
    private String getSysKeyByApiUrl(String strApiUrl) {
        if (StringUtility.isNullOrEmpty(strApiUrl))
            return StringUtility.Empty;
        List<PermissionDO> lstApiPrefix = cacheBp.getSysApiUrlPrefix();
        if (lstApiPrefix == null) {
            lstApiPrefix = permissionMapper.getSysApiUrlPrefix();
            cacheBp.setSysApiUrlPrefix(lstApiPrefix);
        }
        if (lstApiPrefix == null || lstApiPrefix.size() == 0)
            return StringUtility.Empty;

        for (PermissionDO p : lstApiPrefix) {
            String[] arrPrefix = StringUtility.parseObject(p.getApiUrlPrefixJson(), arrEmpty.getClass());
            if (arrPrefix == null || arrPrefix.length == 0)
                continue;
            for (String prefix : arrPrefix) {
                if (strApiUrl.startsWith(prefix))
                    return p.getSysKey();
            }
        }
        return StringUtility.Empty;
    }

    private String[] arrEmpty = new String[0];

    @Autowired
    private IPermitStatBp permitStatBp;

//    /**
//     * ???db???cache??????funcVersion
//     *
//     * @param userName
//     * @param
//     * @return
//     * @author panyun@youkeshu.com
//     * @date 2018???6???14??? ??????4:43:27
//     */
//    public String getFuncVersionFromDbOrCache(String userName) {
//        String funcVersion = cacheBp.getFuncVersion(userName);
//        if (funcVersion == null) {
//            GetAllFuncPermitRespVO ca = permitStatBp.updateUserPermitCache(userName);
//            if (ca != null)
//                return ca.funcVersion;
//        }
//        return funcVersion;
//    }

    /**
     * ?????????????????????api??????
     *
     * @param moduleUrl
     * @param apiUrl
     * @param operator
     * @param sysKey
     * @return
     * @author panyun@youkeshu.com
     * @date 2018???6???14??? ??????3:14:43
     */
    private boolean hasApiFunc(String moduleUrl, String apiUrl, String operator, String sysKey) {
        // ??????????????????????????????????????? jsonA
        String strSysFuncJson = cacheBp.getSysContext(sysKey);
        if (strSysFuncJson == null) {
            strSysFuncJson = this.getSysContextFromDb(sysKey);
            cacheBp.insertSysContext(sysKey, strSysFuncJson);
            if (StringUtility.isNullOrEmpty(strSysFuncJson))
                return true;
        }

        if (StringUtility.Empty.equals(strSysFuncJson))
            return true;

        // apiUrl???A?????????,??????true
        if (!strSysFuncJson.contains(String.format("%s%s%s", "\"", apiUrl, "\"")))
            return true;

        // ?????????????????????????????????funcJson, ?????????????????????,??????NA
        String strCurUserFuncJson = cacheBp.getFuncJson(operator, sysKey);
        if (strCurUserFuncJson == null)
            return false;

        // apiUrl???SubA??????????????????true
        if (strCurUserFuncJson.contains(String.format("%s%s%s", "\"", apiUrl, "\"")))
            return true;
        return false;
    }

    @Autowired
    PermissionMapper permissionMapper;

    private String getSysContextFromDb(String sysKey) {
        PermissionDO per = permissionMapper.getPermissionBySysKey(sysKey);
        if (per == null || per.getSysContext() == null)
            return StringUtility.Empty;
        return per.getSysContext();
    }

    @Override
    public ResultVO addUrcWhiteApi(String json) {
        JSONObject jsonObject = StringUtility.parseString(json).getJSONObject("data");
        String whiteApiUrl = jsonObject.getString("whiteApiUrl");
        UrcWhiteApiVO urcWhiteApiVO = new UrcWhiteApiVO();
        List<String> apiList = JSONArray.parseArray(whiteApiUrl, String.class);
        if (apiList.size() == 0 || apiList == null) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "api????????????");
        }

        for (String whiteApi : apiList) {
//???????????????api????????????????????????????????????????????????
            Integer count = urcWhiteApiUrlMapper.selectWhiteApiByWiteApi(whiteApi);
            if (count != 0) {
                continue;
            }
            urcWhiteApiVO.setWhiteApiUrl(whiteApi);
            urcWhiteApiVO.setCreateBy(sessionBp.getOperator());
            urcWhiteApiVO.setCreateTime(new Date());
            urcWhiteApiVO.setModifiedBy(sessionBp.getOperator());
            urcWhiteApiVO.setModifiedTime(new Date());
            urcWhiteApiUrlMapper.insert(urcWhiteApiVO);
        }

        List<String> ListApi = urcWhiteApiUrlMapper.selectWhiteApi();
        String apiStr = StringUtility.toJSONString(ListApi);
//??????api
        cacheBp.insertWhiteApi(apiStr);
        return VoHelper.getSuccessResult();

    }

    @Override
    public ResultVO deleteWhiteApi(String json) {
        JSONObject jsonObject = StringUtility.parseString(json).getJSONObject("data");
        String whiteApi = jsonObject.getString("whiteApiUrl");
        if (StringUtility.isNullOrEmpty(whiteApi)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "??????api????????????");
        }
        Integer count = urcWhiteApiUrlMapper.selectWhiteApiByWiteApi(whiteApi);
        if (count == 0) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "???????????????api????????????????????????");
        }
        urcWhiteApiUrlMapper.deleteApi(whiteApi);
        List<String> ListApi = urcWhiteApiUrlMapper.selectWhiteApi();
        String apiStr = StringUtility.toJSONString(ListApi);
        //?????????????????????????????????
        cacheBp.insertWhiteApi(apiStr);
        return VoHelper.getSuccessResult();
    }
}
