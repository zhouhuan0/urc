package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.Enum.ModuleCodeEnum;
import com.yks.urc.authway.bp.api.AuthWayBp;
import com.yks.urc.constant.UrcConstant;
import com.yks.urc.dataauthorization.bp.api.DataAuthorization;
import com.yks.urc.entity.*;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.exception.URCServiceException;
import com.yks.urc.fw.HttpUtility2;
import com.yks.urc.fw.StringUtil;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.fw.constant.StringConstant;
import com.yks.urc.log.Log;
import com.yks.urc.mapper.*;
import com.yks.urc.permitStat.bp.api.IPermitRefreshTaskBp;
import com.yks.urc.serialize.bp.api.ISerializeBp;
import com.yks.urc.service.api.IUserService;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.user.bp.api.IUrcLogBp;
import com.yks.urc.user.bp.api.IUserBp;
import com.yks.urc.userValidate.bp.api.IUserValidateBp;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.Query;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserServiceImpl implements IUserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IUserBp userBp;
    @Autowired
    DataAuthorization dataAuthorization;
    @Autowired
    private AuthWayBp authWayBp;
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IUserValidateBp userValidateBp;
    @Autowired
    private IRoleMapper roleMapper;
    @Autowired
    private ISessionBp sessionBp;
    @Autowired
    private IUserMapper iUserMapper;
    @Autowired
    private IRolePermissionMapper rolePermitMapper;
    @Autowired
    private IPermitRefreshTaskBp permitRefreshTaskBp;
    @Autowired
    private IUserRoleMapper userRoleMapper;
    @Autowired
    private ISerializeBp serializeBp;
    @Autowired
    PermitItemPositionMapper permitItemPositionMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUrcLogBp iUrcLogBp;
    @Autowired
    private UrcSystemAdministratorMapper urcSystemAdministratorMapper;
    @Autowired
    private IRolePermissionMapper rolePermissionMapper;

    @Value("${userInfo.resetPwdGetVerificationCode}")
    private String resetPwdGetVerificationCode;
    @Value("${sku.castInfo}")
    private String castInfo;
    @Value("${warehouse.warehouseInfo}")
    private String warehouseInfo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO syncUserInfo(String operator) {
        ResultVO resultVO = new ResultVO();

        try {
        	dataAuthorization.syncShopSite(operator);
            resultVO = userBp.SynUserFromUserInfo(operator);
        } catch (Exception e) {
            logger.error("同步任务异常" + e.getMessage());
            return VoHelper.getErrorResult();
        } finally {
            return resultVO;
        }
    }

    @Override
    public ResultVO login(Map<String, String> map) {
        return userBp.login(map);
    }

    @Override
    public ResultVO logout(String jsonStr) {
        return userBp.logout(jsonStr);
    }

	/*
     * public ResultVO queryUserDataByRuleId(DataRuleDO ruleDO) { List<UserDO>
	 * userList = userMapper.queryUserDataByRuleId(ruleDO); if (userList != null &&
	 * userList.size() > 0) { return VoHelper.getSuccessResult(userList); } return
	 * VoHelper.getErrorResult(); }
	 *
	 * public ResultVO queryUserNoDataByRuleId(DataRuleDO ruleDO) { List<UserDO>
	 * userList = userMapper.queryUserNoDataByRuleId(ruleDO); if (userList != null
	 * && userList.size() > 0) { return VoHelper.getSuccessResult(userList); }
	 * return VoHelper.getErrorResult(); }
	 */


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<PageResultVO> getUsersByUserInfo(String operator, UserVO userVO, String pageNumber, String pageData) {
        //查询用户系统数据管理员
        List<String> list = urcSystemAdministratorMapper.selectSysKeyByAdministratorType(operator, UrcConstant.AdministratorType.dataAdministrator.intValue());
        if (!roleMapper.isAdminOrSuperAdmin(operator)) {
            //首先要判断该用户是否是超级管理员或业务管理员或系统数据管理员
            if(CollectionUtils.isEmpty(list)){
                return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), "非管理员无法查看此数据");
            }
        }
        return userBp.getUsersByUserInfo(operator, userVO, pageNumber, pageData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<List<OmsPlatformVO>> getPlatformList(String operator) {
        ResultVO<List<OmsPlatformVO>> rslt = new ResultVO();
        try {
            if (StringUtility.isNullOrEmpty(operator)) {
                rslt.msg = "操作人员为空 " + operator;
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.data = dataAuthorization.getPlatformList(operator);
            if (rslt.data == null) {
                rslt.msg = "Error,获取的平台为空 " + operator;
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.msg = "Success " + operator;
            rslt.state = CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("未知异常", e);
            throw new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(), "出现未知异常", e);
        } finally {
            return rslt;
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<List<OmsShopVO>> getShopList(String operator, String platform) {
        ResultVO<List<OmsShopVO>> rslt = new ResultVO();
        try {
            if (StringUtility.isNullOrEmpty(operator) || StringUtility.isNullOrEmpty(platform)) {
                return rslt;
            }
            rslt.data = dataAuthorization.getShopList(operator, platform);
            rslt.msg = "Success";
            rslt.state = CommonMessageCodeEnum.SUCCESS.getCode();
            if (rslt.data == null) {
                rslt.msg = "Error 无法找到此平台的店铺信息,或者无此平台," + operator;
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
        } catch (Exception e) {
            logger.error("未知异常", e);
            throw new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(), "出现未知异常", e);
        } finally {
            return rslt;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<List<SysAuthWayVO>> getMyAuthWay(String operator) {
        ResultVO<List<SysAuthWayVO>> rslt = new ResultVO();
        try {
            if (StringUtility.isNullOrEmpty(operator)) {
                return rslt;
            }
            rslt.data = authWayBp.getMyAuthWay(operator);
            if (rslt.data == null) {
                rslt.msg = "Failed ," + operator + "您不是管理员,没有授权权限";
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.msg = "成功, " + operator;
            rslt.state = CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("未知异常", e);
            throw new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(), "出现未知异常", e);
        } finally {
            return rslt;
        }
    }

    @Log("getAllFuncPermit")
    @Override
    public ResultVO<GetAllFuncPermitRespVO> getAllFuncPermit(String jsonStr) {
        try {
            JSONObject jsonObject = StringUtility.parseString(jsonStr);
            String operator = jsonObject.getString(StringConstant.operator);
            SysKeysVO sysKeysVO = new SysKeysVO();
            if(null != jsonObject.getJSONObject(StringConstant.data)){
            	sysKeysVO =StringUtility.parseObject(jsonObject.getJSONObject(StringConstant.data).toString(),SysKeysVO.class);
            }

            return userBp.getAllFuncPermit(operator,null != sysKeysVO ? sysKeysVO.getSysKeys() : null);
        } catch (Exception ex) {
            logger.error(String.format("getAllFuncPermit:%s", jsonStr), ex);
            return VoHelper.getErrorResult();
        }
    }

    @Log("funcPermitValidate")
    @Override
    public ResultVO funcPermitValidate(Map<String, String> map) {
        return userValidateBp.funcPermitValidate(map);
    }


    @Override
    public ResultVO fuzzySearchUsersByUserName(String pageNumber, String pageData, String userName, String operator) {
        UserVO userVO = new UserVO();
        userVO.userName = userName;
        if (!roleMapper.isSuperAdminAccount(operator)) {
            userVO.createBy = operator;
        }
        Query query = new Query(userVO, pageNumber, pageData);
        List<UserVO> userList = userMapper.fuzzySearchUsersByUserName(query);
        long userCount = userMapper.fuzzySearchUsersByUserNameCount(query);
        PageResultVO pageResultVO = new PageResultVO(userList, userCount, pageData);
        return VoHelper.getSuccessResult(pageResultVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<List<UserVO>> getUserByUserName(String operator, UserVO userVO) {
        List<UserVO> userVOS = new ArrayList<>();
        UserVO userVO1 = new UserVO();
        UserDO userDO = userMapper.getUserByUserName(userVO);
        if (userDO == null) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), "用户不存在");
        }
        userVO1.userName = userDO.getUserName();
        userVOS.add(userVO1);
        return VoHelper.getSuccessResult(userVOS);
    }

    @Autowired
    private PlatformMapper platformMapper;
    @Autowired
    private ShopSiteMapper shopSiteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<List<OmsPlatformVO>> getPlatformShopSite(String operator) {
        try {
            List<PlatformDO> platformDOS = platformMapper.selectAll();
            //装载平台volist
            List<OmsPlatformVO> omsPlatformVOS = new ArrayList<>();
            for (PlatformDO platformDO : platformDOS) {
                OmsPlatformVO omsPlatformVO = new OmsPlatformVO();
                omsPlatformVO.platformId = platformDO.getPlatformId();
                omsPlatformVO.platformName = platformDO.getPlatformName();

                List<ShopSiteDO> shopSiteDOS = shopSiteMapper.selectShopSiteByPlatformId(platformDO.getPlatformId());
                if (shopSiteDOS == null || shopSiteDOS.size() == 0) {
                    continue;
                } else {
                    //集合都必须先初识化
                    omsPlatformVO.lstShop = new ArrayList<>(shopSiteDOS.size());
                    for (ShopSiteDO shopSiteDO : shopSiteDOS) {
                        OmsShopVO omsShopVO = new OmsShopVO();
                        //针对速卖通的
                        omsShopVO.shopId = shopSiteDO.getSellerId();
                        omsShopVO.shopName = shopSiteDO.getShop();
                        //如果站点id为空,则list为空
                        if ("".equals(shopSiteDO.getSiteId())) {
                            omsShopVO.lstSite = null;
                        } else {
                            OmsSiteVO omsSiteVO = new OmsSiteVO();
                            omsSiteVO.siteId = shopSiteDO.getSiteId();
                            //如果站点名称为空,则吧站点id赋值给站点名称
                            if ("".equals(shopSiteDO.getSiteName())) {
                                omsSiteVO.siteName = omsSiteVO.siteId;
                            } else {
                                omsSiteVO.siteName = shopSiteDO.getSiteName();
                                omsShopVO.lstSite.add(omsSiteVO);
                            }
                        }
                        omsPlatformVO.lstShop.add(omsShopVO);
                    }
                    omsPlatformVOS.add(omsPlatformVO);
                }
            }
            return VoHelper.getSuccessResult(omsPlatformVOS);
        } catch (Exception e) {
            logger.error("获取数据异常:", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "未知异常");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO syncPlatform(String operator) {
        try {
            dataAuthorization.syncPlatform(operator);
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("同步平台数据出错", e);
            return VoHelper.getErrorResult();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO syncShopSite(String operator) {
        try {
            dataAuthorization.syncShopSite(operator);
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("同步账号站点数据出错", e);
            return VoHelper.getErrorResult();
        }
    }

    @Override
    public ResultVO resetPwdGetVerificationCode(String userName, String mobile) {
        if (StringUtility.isNullOrEmpty(userName)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "用户名不能为空");
        }
        if (StringUtility.isNullOrEmpty(mobile)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "手机号不能为空");
        }
        Map map = new HashMap(10);
        map.put("username", userName);
        map.put("mobile", mobile);
        map.put("get_code", "true");
        String response;
        try {
            response = HttpUtility2.postForm(resetPwdGetVerificationCode, map, null);
        } catch (Exception e) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取验证码失败");
        }
        JSONObject jsonObject = JSONObject.parseObject(response);
        String message = jsonObject.getString("message");
        String error = jsonObject.getString("error");
        if (!StringUtility.isNullOrEmpty(error)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), error);
        }
        return VoHelper.getSuccessResult(message);
    }

    @Override
    public ResultVO resetPwdSubmit(String mobile, String new_password, String username, String code) {
        ResultVO rslt = new ResultVO();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mobile", mobile);
        jsonObject.put("code", code);
        jsonObject.put("username", username);
        jsonObject.put("new_password", new_password);
        jsonObject.put("get_code", false);
        String requestBody = jsonObject.toString();
        Map<String, String> requestHeader = new HashMap();
        requestHeader.put("Content-Type", "application/json");
        String response;
        try {
            response = HttpUtility2.postString(resetPwdGetVerificationCode, requestBody, requestHeader);
        } catch (Exception e) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "重置密码失败。");
        }
        JSONObject jsonObjectResponse = JSONObject.parseObject(response);
        String message = jsonObjectResponse.getString("message");
        String error = jsonObjectResponse.getString("error");
        if (!StringUtility.isNullOrEmpty(error)) {
            return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), error);
        }
        rslt.msg = message;
        rslt.state = CommonMessageCodeEnum.SUCCESS.getCode();
        return rslt;
    }


    @Override
    public ResultVO getBasicDataList(String jsonStr) {
        ResultVO resultVO = new ResultVO();
        String response;
        try {
            JSONObject jsonObject = StringUtility.parseString(jsonStr);
            String operator = jsonObject.getString("operator");
            if (operator == null) {
                logger.error("操作人员不能为空");
                return VoHelper.getResultVO(CommonMessageCodeEnum.PARAM_NULL.getCode(), "操作人员不能为空");
            }
            response = HttpUtility2.postForm(castInfo, null, null);

            List<CategoryResponseVO> categoryResponseVOs  = StringUtility.json2ObjNew(response, new TypeReference<List<CategoryResponseVO>>() {
    		});
            /*JSONArray jsonArray = JSONArray.parseArray(response);
            int size1 = jsonArray.size();
            SkuCategoryVO skuCategoryVO = new SkuCategoryVO();
            //  List<CategoryVO> categoryVOList = new ArrayList<>();
            List<CategoryResponseVO> categoryResponseVOListFirst = new ArrayList<>();
            String cateId;
            String cateNameCn;
            for (int i = 0; i < size1; i++) {
                CategoryResponseVO categoryResponseVO = new CategoryResponseVO();
                List<CategoryResponseVO> categoryResponseVOListSecond = new ArrayList<>();
                //第一级分类
                JSONObject jsonObjectFirst = StringUtility.parseString(StringUtility.toJSONString(jsonArray.get(i)));
                cateId = jsonObjectFirst.getString("cateId");
                cateNameCn = jsonObjectFirst.getString("cateNameCn");
                categoryResponseVO.setCateId(cateId);
                categoryResponseVO.setCateNameCn(cateNameCn);
                JSONArray jsonArraySceond = jsonObjectFirst.getJSONArray("subCategorys");
                int size2 = jsonArraySceond.size();
                for (int j = 0; j < size2; j++) {
                    CategoryResponseVO categoryResponseVOSecond = new CategoryResponseVO();
                    List<CategoryResponseVO> categoryResponseVOListThird = new ArrayList<>();
                    JSONObject jsonObjectSecond = StringUtility.parseString(StringUtility.toJSONString(jsonArraySceond.get(j)));
                    cateId = jsonObjectSecond.getString("cateId");
                    cateNameCn = jsonObjectSecond.getString("cateNameCn");
                    categoryResponseVOSecond.setCateId(cateId);
                    categoryResponseVOSecond.setCateNameCn(cateNameCn);
                    JSONArray jsonArrayThird = jsonObjectSecond.getJSONArray("subCategorys");
                    int size3 = jsonArrayThird.size();

                    for (int k = 0; k < size3; k++) {
                        CategoryResponseVO categoryResponseVOThird = new CategoryResponseVO();
                        JSONObject jsonObjectThird = StringUtility.parseString(StringUtility.toJSONString(jsonArrayThird.get(k)));
                        cateId = jsonObjectThird.getString("cateId");
                        cateNameCn = jsonObjectThird.getString("cateNameCn");
                        categoryResponseVOThird.setCateId(cateId);
                        categoryResponseVOThird.setCateNameCn(cateNameCn);
                        categoryResponseVOListThird.add(categoryResponseVOThird);
                    }
                    categoryResponseVOSecond.setChildren(categoryResponseVOListThird);
                    categoryResponseVOListSecond.add(categoryResponseVOSecond);
                }
                categoryResponseVO.setChildren(categoryResponseVOListSecond);
                categoryResponseVOListFirst.add(categoryResponseVO);
            }*/
            //组装返回数据basicDataVO
            BasicDataVO basicDataVO = getBasicDataVO(new SkuCategoryVO(), categoryResponseVOs);
            resultVO.msg = "操作成功";
            resultVO.data = basicDataVO;
            resultVO.state = CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("获取sku分类,库存等数据权限失败", e);
            return VoHelper.getErrorResult();
        }
        return resultVO;
    }


    private BasicDataVO getBasicDataVO(SkuCategoryVO skuCategoryVO, List<CategoryResponseVO> categoryResponseVOList) {
        skuCategoryVO.setFirstCategory(categoryResponseVOList);
        List<DataColumnVO> dataColumnVOList = new ArrayList<>();
        DataColumnVO chineseNameDataColumnVO = new DataColumnVO();
        chineseNameDataColumnVO.setKey("chineseName");
        chineseNameDataColumnVO.setName("中文名称");
        DataColumnVO costPriceDataColumnVO = new DataColumnVO();
        costPriceDataColumnVO.setKey("costPrice");
        costPriceDataColumnVO.setName("成本价");
        DataColumnVO availableStockDataColumnVO = new DataColumnVO();
        availableStockDataColumnVO.setKey("availableStock");
        availableStockDataColumnVO.setName("可用库存");
        DataColumnVO logisticsPropertiesDataColumnVO = new DataColumnVO();
        logisticsPropertiesDataColumnVO.setKey("logisticsProperties");
        logisticsPropertiesDataColumnVO.setName("物流属性");
        dataColumnVOList.add(chineseNameDataColumnVO);
        dataColumnVOList.add(costPriceDataColumnVO);
        dataColumnVOList.add(availableStockDataColumnVO);
        dataColumnVOList.add(logisticsPropertiesDataColumnVO);
        skuCategoryVO.setDataColumn(dataColumnVOList);
        BasicDataVO basicDataVO = new BasicDataVO();
        basicDataVO.setBasicData(skuCategoryVO);
        return basicDataVO;
    }

    @Override
    public ResultVO getWarehouse(String jsonStr) {
        ResultVO resultVO = new ResultVO();
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String operator = jsonObject.getString("operator");
        if (operator == null) {
            logger.error("操作人员不能为空");
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "操作人员不能为空");
        }
        List<Object> list = new ArrayList();
        try {
            String response = HttpUtility2.postForm(warehouseInfo, null, null);
            JSONObject jsonObjectResponse = StringUtility.parseString(response);
            JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
            int size = jsonArray.size();
            for (int i = 0; i < size; i++) {
                WarehourseResponseVO warehourseResponseVO = new WarehourseResponseVO();
                JSONObject jsonObjectToWeb = StringUtility.parseString(StringUtility.toJSONString(jsonArray.get(i)));
                if (!StringUtility.isNullOrEmpty(jsonObjectToWeb.getString("code"))) {
                    warehourseResponseVO.setLabel(jsonObjectToWeb.getString("name"));
                    warehourseResponseVO.setValue(jsonObjectToWeb.getString("code"));
                    list.add(warehourseResponseVO);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to get warehousing data authorization:%s", jsonStr), e);
            return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), "获取仓储数据授权失败！");
        }
        resultVO.state = CommonMessageCodeEnum.SUCCESS.getCode();
        resultVO.data = list;
        resultVO.msg = "获取成功";
        return resultVO;
    }

    @Override
    public ResultVO searchUserPerson(String jsonStr) {
        ResultVO resultVO = new ResultVO();
        List<UserAndPersonDO> userAndPersonDOS = new ArrayList<>();
        try {
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            String searchContext = jsonObject.getString("searchContext");
            Integer pageData = jsonObject.getInteger("pageData");
            if (pageData == null) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "每页的条数不能为空");
            }
            Integer pageNumber = jsonObject.getInteger("pageNumber");
            if (pageNumber == null) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "页码不能为空");
            }
            if (pageData <= 0 || pageData == null) {
                pageData = 10;
            }
            if (pageNumber <= 0 || pageNumber == null) {
                pageNumber = 1;
            }
            int currIndex = (pageNumber - 1) * pageData;
            int currSize = pageData;
            UserPersonParamDO userPersonParamDO = new UserPersonParamDO();
            userPersonParamDO.currIndex = currIndex;
            userPersonParamDO.currSize = currSize;
            userPersonParamDO.searchContext = searchContext;
            userAndPersonDOS = iUserMapper.selectUserNameAndPeronNameByUserName(userPersonParamDO);
        } catch (Exception e) {
            logger.error(String.format("Search user online account and user name failed:%s", jsonStr), e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "搜索用户上网账号和用户名失败");
        }
        resultVO.data = userAndPersonDOS;
        resultVO.msg = "搜索用户上网账号和用户名成功";
        resultVO.state = CommonMessageCodeEnum.SUCCESS.getCode();
        return resultVO;
    }

    @Override
    public ResultVO searchMatchUserPerson(String jsonStr) {
        try {
            String keys = StringUtility.parseString(jsonStr).getString("keys");
            if(StringUtility.isNullOrEmpty(keys)){
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
            }
            List<String> lstUserName = JSONArray.parseArray(keys, String.class);
            Map<String, Object> resultMap = new HashMap<>();
            List<UserAndPersonDO> userAndPersonDOS = iUserMapper.selectUserNameAndPeronName(lstUserName);
            List<UserAndPersonDO> notOkList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(userAndPersonDOS)){
                List<String> collect = userAndPersonDOS.stream().map(UserAndPersonDO::getUserName).collect(Collectors.toList());
                lstUserName.removeAll(collect);
            }

            for (String s : lstUserName) {
                UserAndPersonDO userAndPersonDO = new UserAndPersonDO();
                userAndPersonDO.setUserName(s);
                notOkList.add(userAndPersonDO);
            }
            resultMap.put("notOkList",notOkList);
            resultMap.put("okList",userAndPersonDOS == null ? Collections.EMPTY_LIST : userAndPersonDOS);
            return VoHelper.getSuccessResult(resultMap);
        } catch (Exception e) {
            logger.error("searchMatchUserPerson error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "精确匹配搜索用户账号失败");
        }
    }

    @Override
    public ResultVO getUserByPosition(String jsonStr) {
        try {
            /* 1、将json字符串转为Json对象 */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //获取岗位id
            String positionIdStr = jsonObject.getString("positionIds");
            if(StringUtility.isNullOrEmpty(positionIdStr)){
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
            }
            List<String> positionIds = JSONArray.parseArray(positionIdStr, String.class);
            /*组装查询条件queryMap*/
            Map<String, Object> queryMap = new HashMap<>();
            int pageNumber = jsonObject.getInteger("pageNumber");
            int pageData = jsonObject.getInteger("pageData");
            if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
                throw new URCBizException("pageNumber or  pageData is not a num", ErrorCode.E_000003);
            }
            int currPage = pageNumber;
            int pageSize = pageData;
            queryMap.put("currIndex", (currPage - 1) * pageSize);
            queryMap.put("pageSize", pageSize);
            queryMap.put("positionIds", positionIds);
            //获得数据
            List<UserByPosition> list = iUserMapper.getUserByPosition(queryMap);
            //获得总数
            int total = iUserMapper.getUserByPositionCount(queryMap);;
            PageResultVO pageResultVO = new PageResultVO(list, total, queryMap.get("pageSize").toString());
            return VoHelper.getSuccessResult(pageResultVO);
        } catch (Exception e) {
            logger.error("getUserByPosition error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取岗位用户列表失败");
        }
    }

    @Override
    public ResultVO setSupperAdmin(String jsonStr,String operator) {
        try{
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //String operator = jsonObject.getString("operator");

            if (StringUtility.isNullOrEmpty(operator)) {
                throw new URCBizException("operator为空", ErrorCode.E_000002);
            }

            if (StringUtility.isNullOrEmpty(jsonObject.getString("positionId"))) {
                throw new URCBizException("positionId为空", ErrorCode.E_000002);
            }

            //判断用户是不是超级管理员
            boolean isSuperAdmin = roleMapper.isSuperAdminAccount(operator);
            if(!isSuperAdmin) {
                return VoHelper.getFail("不是超管用户不能操作");
            }
            long positionId = jsonObject.getLong("positionId");
            //判断是不是在权限组
            boolean isGroupAccount = roleMapper.isGroupAccount(positionId);
            if (!isGroupAccount) {
                String isSupperAdmin = jsonObject.getString("isSupperAdmin");
                RoleDO role = new RoleDO();
                //岗位id
                role.setRoleId(positionId);
                List<PermissionDO> permissionDOList = new ArrayList<>();
                //是否是超管
                if ("0".equals(isSupperAdmin)) {
                    //0 :否 1:是
                    role.setIsAuthorizable(0);
                } else if ("1".equals(isSupperAdmin)) {
                    role.setIsAuthorizable(2);
                    //设为超管拥有所有启用的系统权限
                    permissionDOList = permissionMapper.getAllSysKey();
                }
                //修改人
                role.setModifiedBy(operator);
                iUserMapper.setSupperAdmin(role);
                //保存岗位功能权限
                userService.doSavePositionPermission(permissionDOList,positionId,null);
                UrcLog urcLog = new UrcLog(sessionBp.getOperator(), ModuleCodeEnum.ROLE_MANAGERMENT.getStatus(), "岗位设置超管",String.format("%s -> %s",roleMapper.getRoleName(positionId),StringUtility.stringEqualsIgnoreCase("1",isSupperAdmin) ? "是":"否"), jsonStr);
                iUrcLogBp.insertUrcLog(urcLog);
                return VoHelper.getSuccessResult();
            }else{
                return VoHelper.getFail("该岗位已存在权限组中不能操作");
            }
        } catch (Exception e) {
            logger.error("setSupperAdmin error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "设为超管失败失败");
        }
    }

    @Override
    public ResultVO getUserAuthorizablePermissionForPosition(String jsonStr,String operator) {
        try{
            JSONObject jsonObject = StringUtility.parseString(jsonStr);
            //String operator = jsonObject.getString("operator");

            if (StringUtility.isNullOrEmpty(operator)) {
                throw new URCBizException("operator为空", ErrorCode.E_000002);
            }
            List<PermissionDO> permissionVOs = new ArrayList<>();
            //有超管岗位就可以拥有所有功能权限
            if (roleMapper.isSuperAdminAccount(operator)) {
                permissionVOs = permissionMapper.getAllSysKey();
            }else {
                //通过当前用户获得权限
                permissionVOs = iUserMapper.getUserAuthorizablePermissionForPosition(operator);
            }
            return VoHelper.getSuccessResult(permissionVOs);
        } catch (Exception e) {
            logger.error("getUserAuthorizablePermissionForPosition error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "获取权限列表失败");
        }
    }

    @Override
    public ResultVO savePositionPermission(String jsonStr) {
        try{
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            Long positionId = jsonObject.getLong("positionId");

            if (positionId == null) {
                throw new URCBizException("positionId为空", ErrorCode.E_000002);
            }
            List<PermissionDO> permissionDOList = serializeBp.json2ObjNew(jsonObject.getString("selectedContext"), new TypeReference<List<PermissionDO>>() {
            });
            //判断用户是不是超级管理员
            boolean isSuperAdmin = roleMapper.isSuperAdminAccount(sessionBp.getOperator());
            //保存岗位功能权限
            userService.doSavePositionPermission(permissionDOList,positionId,isSuperAdmin? null : sessionBp.getOperator());
            //保存操作日志
            UrcLog urcLog = new UrcLog(sessionBp.getOperator(), ModuleCodeEnum.ROLE_MANAGERMENT.getStatus(), "岗位分配权限",roleMapper.getRoleName(positionId) , jsonStr);
            iUrcLogBp.insertUrcLog(urcLog);
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("savePositionPermission error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "保存岗位功能权限失败,"+e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void doSavePositionPermission(List<PermissionDO> permissionDOList, long positionId, String operator) throws Exception {
        List<String> roleSysKey = new ArrayList<String>();
        if (!StringUtil.isEmpty(operator)) {
            //查询用户可以授权的系统
            roleSysKey = urcSystemAdministratorMapper.selectSysKeyByAdministratorType(operator, UrcConstant.AdministratorType.functionAdministrator.intValue());
            //不是超管也不是任何系统的功能管理员时直接抛异常
            if(CollectionUtils.isEmpty(roleSysKey)){
                throw new Exception("当前用户不是任何系统功能管理员,无法分配权限!");
            }
        }

        //通过当前用户获得权限
        List<RolePermissionDO> lstRolePermit = new ArrayList<>();
        for (PermissionDO permissionDO : permissionDOList) {
            RolePermissionDO rp = new RolePermissionDO();
            rp.setRoleId(positionId);
            rp.setSysKey(permissionDO.getSysKey());
            rp.setSelectedContext(permissionDO.getSysContext());
            rp.setCreateTime(new Date());
            rp.setCreateBy(sessionBp.getOperator());
            rp.setModifiedBy(sessionBp.getOperator());
            rp.setModifiedTime(rp.getCreateTime());
            lstRolePermit.add(rp);
        }
        //先删除岗位权限,在插入
        rolePermitMapper.deleteByRoleIdInSysKey(positionId + "", roleSysKey);
        if(!CollectionUtils.isEmpty(lstRolePermit)){
            rolePermitMapper.insertBatch(lstRolePermit);
        }

        RolePermissionDO permissionDO = new RolePermissionDO();
        permissionDO.setRoleId(positionId);
        List<RolePermissionDO> rolePermissionList = rolePermissionMapper.getRoleSuperAdminPermission(permissionDO);
        List<String> list = new ArrayList<>();
        for (RolePermissionDO rolePermissionDO : rolePermissionList) {
            //拼接权限字符串
            list.addAll(concatData(rolePermissionDO.getSelectedContext()));
        }
        //写入关联表
        if (!CollectionUtils.isEmpty(list)) {
            List<PermitItemPosition> param = list.stream().map(e -> {
                PermitItemPosition vo = new PermitItemPosition();
                vo.setPermitKey(e);
                vo.setPositionId(positionId);
                vo.setModifier(sessionBp.getOperator());
                vo.setCreator(sessionBp.getOperator());
                vo.setCreatedTime(new Date());
                vo.setModifiedTime(new Date());
                return vo;
            }).collect(Collectors.toList());
            //先删后插
            permitItemPositionMapper.deleteBypositionId(positionId);
            permitItemPositionMapper.insertPosition(param);
        }


        //获取角色原关联的用户userName
        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setRoleId(positionId);
        List<String> oldRelationUsers = userRoleMapper.getUserNameByRoleId(userRoleDO);
        //更新用户功能权限缓存
        List<String> lstUserName = new ArrayList<>();
        //添加角色原来关联的用户列表
        if (oldRelationUsers != null && !oldRelationUsers.isEmpty()) {
            lstUserName.addAll(oldRelationUsers);
        }
        if (!lstUserName.isEmpty()) {
            /*去重*/
            lstUserName = removeDuplicate(lstUserName);
            //保存权限改变的用户
            // 改为由定时任务执行
            permitRefreshTaskBp.addPermitRefreshTask(lstUserName);
        }
    }

    public List<String> concatData(String sysContext){
        List<String> list = new ArrayList<String>();
        Set<FunctionVO> lstAllKey = new HashSet<>();
        if (StringUtils.isBlank(sysContext)) {
            return list;
        }
        SystemRootVO rootVO = serializeBp.json2ObjNew(sysContext, new TypeReference<SystemRootVO>() {
        });
        lstAllKey.addAll(getAllPermitItem(rootVO));
        list = lstAllKey.stream().map(e->e.key).collect(Collectors.toList());
        return list;
        }

    private void scanMenu(String sysName, SystemRootVO rootVO, Set<FunctionVO> lstAllKey) {
        List<MenuVO> menu1 = rootVO.menu;
        if (CollectionUtils.isEmpty(menu1)) {
            return;
        }
        for (int j = 0; j < menu1.size(); j++) {
            MenuVO curMemu = menu1.get(j);
//            lstAllKey.add(curMemu.key);
            scanModule(String.format("%s-%s", sysName, curMemu.name), curMemu.module, lstAllKey);
        }
    }

    private void scanModule(String parentName, List<ModuleVO> lstModule, Set<FunctionVO> lstAllKey) {
        if (CollectionUtils.isEmpty(lstModule)) {
            return;
        }
        for (ModuleVO moduleVO : lstModule) {
            if (CollectionUtils.isEmpty(moduleVO.module) && CollectionUtils.isEmpty(moduleVO.function)) {
                FunctionVO fKey = new FunctionVO();
                fKey.key = moduleVO.key;
                fKey.name = String.format("%s-%s", parentName, moduleVO.name);
                lstAllKey.add(fKey);
            } else {
                scanModule(String.format("%s-%s", parentName, moduleVO.name), moduleVO.module, lstAllKey);
                scanFunction(String.format("%s-%s", parentName, moduleVO.name), moduleVO.function, lstAllKey);
            }
        }
    }
    private Set<FunctionVO> getAllPermitItem(SystemRootVO rootVO) {
        Set<FunctionVO> lstAllKey = new HashSet<>();
        scanMenu(rootVO.system.name, rootVO, lstAllKey);
        return lstAllKey;
    }

    private void scanFunction(String parentName, List<FunctionVO> lstFunction, Set<FunctionVO> lstAllKey) {
        if (CollectionUtils.isEmpty(lstFunction)) {
            return;
        }
        for (FunctionVO f : lstFunction) {
            if (CollectionUtils.isEmpty(f.function)) {
                f.name = String.format("%s-%s", parentName, f.name);
                lstAllKey.add(f);
            } else {
                scanFunction(f.name, f.function, lstAllKey);
            }
        }
    }
    /**
     * 去重
     */
    private List<String> removeDuplicate(List<String> lstUserName) {
        HashSet h = new HashSet(lstUserName);
        lstUserName.clear();
        lstUserName.addAll(h);
        return lstUserName;
    }

    @Log("getAllFuncPermitForOtherSystem")
    @Override
    public ResultVO getAllFuncPermitForOtherSystem(String jsonStr) {
        try {
            JSONObject jsonObject = StringUtility.parseString(jsonStr);
            String operator = jsonObject.getString(StringConstant.operator);
            SysKeysVO sysKeysVO = new SysKeysVO();
            if(null != jsonObject.getJSONObject(StringConstant.data)){
                sysKeysVO =StringUtility.parseObject(jsonObject.getJSONObject(StringConstant.data).toString(),SysKeysVO.class);
            }
            return userBp.getAllFuncPermitForOtherSystem(operator,null != sysKeysVO ? sysKeysVO.getSysKeys() : null);
        } catch (Exception ex) {
            logger.error(String.format("getAllFuncPermitForOtherSystem:%s", jsonStr), ex);
            return VoHelper.getErrorResult();
        }
    }
}


