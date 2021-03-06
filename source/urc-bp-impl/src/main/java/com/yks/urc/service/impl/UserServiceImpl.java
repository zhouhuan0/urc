package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.Enum.ModuleCodeEnum;
import com.yks.urc.authway.bp.api.AuthWayBp;
import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.constant.UrcConstant;
import com.yks.urc.dataauthorization.bp.api.DataAuthorization;
import com.yks.urc.entity.*;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.exception.URCServiceException;
import com.yks.urc.funcjsontree.bp.api.IFuncJsonTreeBp;
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
    @Autowired
    private IFuncJsonTreeBp funcJsonTreeBp;
    @Autowired
    private IConfigBp configBp;

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
            logger.error("??????????????????" + e.getMessage());
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
        //?????????????????????????????????
        List<String> list = urcSystemAdministratorMapper.selectSysKeyByAdministratorType(operator, UrcConstant.AdministratorType.dataAdministrator.intValue(),null);
        if (!roleMapper.isAdminOrSuperAdmin(operator)) {
            //??????????????????????????????????????????????????????????????????????????????????????????
            if(CollectionUtils.isEmpty(list)){
                return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), "?????????????????????????????????");
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
                rslt.msg = "?????????????????? " + operator;
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.data = dataAuthorization.getPlatformList(operator);
            if (rslt.data == null) {
                rslt.msg = "Error,????????????????????? " + operator;
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.msg = "Success " + operator;
            rslt.state = CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("????????????", e);
            throw new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(), "??????????????????", e);
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
                rslt.msg = "Error ????????????????????????????????????,??????????????????," + operator;
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
        } catch (Exception e) {
            logger.error("????????????", e);
            throw new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(), "??????????????????", e);
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
                rslt.msg = "Failed ," + operator + "??????????????????,??????????????????";
                rslt.state = CommonMessageCodeEnum.FAIL.getCode();
                return rslt;
            }
            rslt.msg = "??????, " + operator;
            rslt.state = CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("????????????", e);
            throw new URCServiceException(CommonMessageCodeEnum.UNKOWN_ERROR.getCode(), "??????????????????", e);
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
            	sysKeysVO =StringUtility.parseObject(jsonObject.getString(StringConstant.data),SysKeysVO.class);
            }
            //????????????:0:erp?????? 1:FBA?????? (???????????????????????????0)
            Integer sysType = sysKeysVO.getSysType();
            if(sysType == null){
                sysType =UrcConstant.SysType.ERP;
            }
            return userBp.getAllFuncPermit(operator,null != sysKeysVO ? sysKeysVO.getSysKeys() : null,sysType);
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
            return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), "???????????????");
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
            //????????????volist
            List<OmsPlatformVO> omsPlatformVOS = new ArrayList<>();
            for (PlatformDO platformDO : platformDOS) {
                OmsPlatformVO omsPlatformVO = new OmsPlatformVO();
                omsPlatformVO.platformId = platformDO.getPlatformId();
                omsPlatformVO.platformName = platformDO.getPlatformName();

                List<ShopSiteDO> shopSiteDOS = shopSiteMapper.selectShopSiteByPlatformId(platformDO.getPlatformId());
                if (shopSiteDOS == null || shopSiteDOS.size() == 0) {
                    continue;
                } else {
                    //???????????????????????????
                    omsPlatformVO.lstShop = new ArrayList<>(shopSiteDOS.size());
                    for (ShopSiteDO shopSiteDO : shopSiteDOS) {
                        OmsShopVO omsShopVO = new OmsShopVO();
                        //??????????????????
                        omsShopVO.shopId = shopSiteDO.getSellerId();
                        omsShopVO.shopName = shopSiteDO.getShop();
                        //????????????id??????,???list??????
                        if ("".equals(shopSiteDO.getSiteId())) {
                            omsShopVO.lstSite = null;
                        } else {
                            OmsSiteVO omsSiteVO = new OmsSiteVO();
                            omsSiteVO.siteId = shopSiteDO.getSiteId();
                            //????????????????????????,????????????id?????????????????????
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
            logger.error("??????????????????:", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "????????????");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO syncPlatform(String operator) {
        try {
            dataAuthorization.syncPlatform(operator);
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("????????????????????????", e);
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
            logger.error("??????????????????????????????", e);
            return VoHelper.getErrorResult();
        }
    }

    @Override
    public ResultVO resetPwdGetVerificationCode(String userName, String mobile) {
        if (StringUtility.isNullOrEmpty(userName)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "?????????????????????");
        }
        if (StringUtility.isNullOrEmpty(mobile)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "?????????????????????");
        }
        Map map = new HashMap(10);
        map.put("username", userName);
        map.put("mobile", mobile);
        map.put("get_code", "true");
        String response;
        try {
            response = HttpUtility2.postForm(resetPwdGetVerificationCode, map, null);
        } catch (Exception e) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "?????????????????????");
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
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "?????????????????????");
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
                logger.error("????????????????????????");
                return VoHelper.getResultVO(CommonMessageCodeEnum.PARAM_NULL.getCode(), "????????????????????????");
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
                //???????????????
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
            //??????????????????basicDataVO
            BasicDataVO basicDataVO = getBasicDataVO(new SkuCategoryVO(), categoryResponseVOs);
            resultVO.msg = "????????????";
            resultVO.data = basicDataVO;
            resultVO.state = CommonMessageCodeEnum.SUCCESS.getCode();
        } catch (Exception e) {
            logger.error("??????sku??????,???????????????????????????", e);
            return VoHelper.getErrorResult();
        }
        return resultVO;
    }


    private BasicDataVO getBasicDataVO(SkuCategoryVO skuCategoryVO, List<CategoryResponseVO> categoryResponseVOList) {
        skuCategoryVO.setFirstCategory(categoryResponseVOList);
        List<DataColumnVO> dataColumnVOList = new ArrayList<>();
        DataColumnVO chineseNameDataColumnVO = new DataColumnVO();
        chineseNameDataColumnVO.setKey("chineseName");
        chineseNameDataColumnVO.setName("????????????");
        DataColumnVO costPriceDataColumnVO = new DataColumnVO();
        costPriceDataColumnVO.setKey("costPrice");
        costPriceDataColumnVO.setName("?????????");
        DataColumnVO availableStockDataColumnVO = new DataColumnVO();
        availableStockDataColumnVO.setKey("availableStock");
        availableStockDataColumnVO.setName("????????????");
        DataColumnVO logisticsPropertiesDataColumnVO = new DataColumnVO();
        logisticsPropertiesDataColumnVO.setKey("logisticsProperties");
        logisticsPropertiesDataColumnVO.setName("????????????");
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
            logger.error("????????????????????????");
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "????????????????????????");
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
            return VoHelper.getResultVO(CommonMessageCodeEnum.FAIL.getCode(), "?????????????????????????????????");
        }
        resultVO.state = CommonMessageCodeEnum.SUCCESS.getCode();
        resultVO.data = list;
        resultVO.msg = "????????????";
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
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "???????????????????????????");
            }
            Integer pageNumber = jsonObject.getInteger("pageNumber");
            if (pageNumber == null) {
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), "??????????????????");
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
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "??????????????????????????????????????????");
        }
        resultVO.data = userAndPersonDOS;
        resultVO.msg = "??????????????????????????????????????????";
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
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "????????????????????????????????????");
        }
    }

    @Override
    public ResultVO getUserByPosition(String jsonStr) {
        try {
            /* 1??????json???????????????Json?????? */
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //????????????id
            String positionIdStr = jsonObject.getString("positionIds");
            if(StringUtility.isNullOrEmpty(positionIdStr)){
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
            }
            List<String> positionIds = JSONArray.parseArray(positionIdStr, String.class);
            /*??????????????????queryMap*/
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
            //????????????
            List<UserByPosition> list = iUserMapper.getUserByPosition(queryMap);
            //????????????
            int total = iUserMapper.getUserByPositionCount(queryMap);;
            PageResultVO pageResultVO = new PageResultVO(list, total, queryMap.get("pageSize").toString());
            return VoHelper.getSuccessResult(pageResultVO);
        } catch (Exception e) {
            logger.error("getUserByPosition error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "??????????????????????????????");
        }
    }

    @Override
    public ResultVO setSupperAdmin(String jsonStr,String operator) {
        try{
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            //Integer sysType = jsonObject.getInteger("sysType");

            if (StringUtility.isNullOrEmpty(operator)) {
                throw new URCBizException("operator??????", ErrorCode.E_000002);
            }

            if (StringUtility.isNullOrEmpty(jsonObject.getString("positionId"))) {
                throw new URCBizException("positionId??????", ErrorCode.E_000002);
            }

            //????????????????????????????????????
            boolean isSuperAdmin = roleMapper.isSuperAdminAccount(operator);
            if(!isSuperAdmin) {
                return VoHelper.getFail("??????????????????????????????");
            }
            long positionId = jsonObject.getLong("positionId");
            //???????????????????????????
            boolean isGroupAccount = roleMapper.isGroupAccount(positionId);
            if (!isGroupAccount) {
                String isSupperAdmin = jsonObject.getString("isSupperAdmin");
                RoleDO role = new RoleDO();
                //??????id
                role.setRoleId(positionId);
                List<PermissionDO> permissionDOList = new ArrayList<>();
                //???????????????
                if ("0".equals(isSupperAdmin)) {
                    //0 :??? 1:???
                    role.setIsAuthorizable(0);
                } else if ("1".equals(isSupperAdmin)) {
                    role.setIsAuthorizable(2);
                    //?????????????????????????????????????????????
                    permissionDOList = permissionMapper.getAllSysKey();
                }
                //?????????
                role.setModifiedBy(operator);
                iUserMapper.setSupperAdmin(role);
                //????????????????????????
                userService.doSavePositionPermission(permissionDOList,positionId,operator,true,null);
                UrcLog urcLog = new UrcLog(sessionBp.getOperator(), ModuleCodeEnum.ROLE_MANAGERMENT.getStatus(), "??????????????????",String.format("%s -> %s",roleMapper.getRoleName(positionId),StringUtility.stringEqualsIgnoreCase("1",isSupperAdmin) ? "???":"???"), jsonStr);
                iUrcLogBp.insertUrcLog(urcLog);
                return VoHelper.getSuccessResult();
            }else{
                return VoHelper.getFail("??????????????????????????????????????????");
            }
        } catch (Exception e) {
            logger.error("setSupperAdmin error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "????????????????????????");
        }
    }

    @Override
    public ResultVO getUserAuthorizablePermissionForPosition(String jsonStr,String operator) {
        try{
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            String sysType = null;
            if(null != jsonObject){
                sysType = jsonObject.getString("sysType");
            }
            if (StringUtility.isNullOrEmpty(operator)) {
                throw new URCBizException("operator??????", ErrorCode.E_000002);
            }
            List<PermissionDO> permissionVOs = new ArrayList<>();
            //????????????????????????????????????????????????
            if (roleMapper.isSuperAdminAccount(operator)) {
                if(StringUtils.isEmpty(sysType)) {
                    permissionVOs = permissionMapper.getAllSysKey();
                }else {
                    permissionVOs = permissionMapper.getSysKey(sysType);
                }
            }else {
                //???????????????????????????????????????,????????????
                String roleId = configBp.getString("special_position");
                if(!StringUtility.isNullOrEmpty(roleId) && userRoleMapper.existsUserName(roleId,operator)){
                    permissionVOs = rolePermissionMapper.getPositionPermission(roleId,sysType);
                }else{
                    //??????????????????????????????
                    if(StringUtils.isEmpty(sysType)) {
                        permissionVOs = iUserMapper.getUserAuthorizablePermissionForPosition(operator);
                    }else {
                        permissionVOs = iUserMapper.getUserPermission(operator, sysType);
                    }
                }
            }
            return VoHelper.getSuccessResult(permissionVOs);
        } catch (Exception e) {
            logger.error("getUserAuthorizablePermissionForPosition error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "????????????????????????");
        }
    }

    @Override
    public ResultVO savePositionPermission(String jsonStr) {
        try{
            JSONObject jsonObject = StringUtility.parseString(jsonStr).getJSONObject("data");
            Long positionId = jsonObject.getLong("positionId");
            Integer sysType = jsonObject.getInteger("sysType");

            if (positionId == null) {
                throw new URCBizException("positionId??????", ErrorCode.E_000002);
            }
            List<PermissionDO> permissionDOList = serializeBp.json2ObjNew(jsonObject.getString("selectedContext"), new TypeReference<List<PermissionDO>>() {
            });
            //????????????????????????????????????
            boolean isSuperAdmin = roleMapper.isSuperAdminAccount(sessionBp.getOperator());
            //????????????????????????
            userService.doSavePositionPermission(permissionDOList,positionId,sessionBp.getOperator(),isSuperAdmin,sysType);
            //??????????????????
            UrcLog urcLog = new UrcLog(sessionBp.getOperator(), ModuleCodeEnum.ROLE_MANAGERMENT.getStatus(), "??????????????????",roleMapper.getRoleName(positionId) , jsonStr);
            iUrcLogBp.insertUrcLog(urcLog);
            return VoHelper.getSuccessResult();
        } catch (Exception e) {
            logger.error("savePositionPermission error!", e);
            return VoHelper.getErrorResult(CommonMessageCodeEnum.FAIL.getCode(), "??????????????????????????????,"+e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void doSavePositionPermission(List<PermissionDO> permissionDOList, long positionId, String operator,boolean isSuper,Integer sysType) throws Exception {
        List<String> roleSysKey = new ArrayList<String>();
        if (!isSuper) {
            //?????????????????????????????????
            String roleId = configBp.getString("special_position");
            //???????????????????????????????????????,????????????
            if(!StringUtility.isNullOrEmpty(roleId) && userRoleMapper.existsUserName(roleId,operator)){
                roleSysKey = rolePermissionMapper.getPositionPermission(roleId,sysType == null ? null : sysType.toString()).stream().map(PermissionDO :: getSysKey).collect(Collectors.toList());
            }else{
                roleSysKey = urcSystemAdministratorMapper.selectSysKeyByAdministratorType(operator, UrcConstant.AdministratorType.functionAdministrator.intValue(),sysType);
            }
            //?????????????????????????????????????????????????????????????????????
            if(CollectionUtils.isEmpty(roleSysKey)){
                throw new Exception("?????????????????????????????????????????????,??????????????????!");
            }
        }else{
            //????????????????????????????????????
            roleSysKey = permitItemPositionMapper.findOneSystemKey(sysType);
        }

        //??????????????????????????????
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
        //?????????????????????,?????????
        rolePermitMapper.deleteByRoleIdInSysKey(positionId + "", roleSysKey);
        if(!CollectionUtils.isEmpty(lstRolePermit)){
            rolePermitMapper.insertBatch(lstRolePermit);
        }

        RolePermissionDO permissionDO = new RolePermissionDO();
        permissionDO.setRoleId(positionId);
        permissionDO.setSysType(sysType);
        permissionDO.setSysKeys(roleSysKey);
        List<RolePermissionDO> rolePermissionList = rolePermissionMapper.getRoleSuperAdminPermissionBySysType(permissionDO);
        List<String> list = new ArrayList<>();
        for (RolePermissionDO rolePermissionDO : rolePermissionList) {
            //?????????????????????
            list.addAll(funcJsonTreeBp.concatData(rolePermissionDO.getSelectedContext()));
        }
        //???????????????
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
            //sys_key ????????? permit_key
            List<String> permitKeys = changeKey(roleSysKey);
            //????????????
            permitItemPositionMapper.deleteBypositionIdAndKey(positionId,permitKeys);
            permitItemPositionMapper.insertPosition(param);
        }


        //??????????????????????????????userName
        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setRoleId(positionId);
        List<String> oldRelationUsers = userRoleMapper.getUserNameByRoleId(userRoleDO);
        //??????????????????????????????
        List<String> lstUserName = new ArrayList<>();
        //???????????????????????????????????????
        if (oldRelationUsers != null && !oldRelationUsers.isEmpty()) {
            lstUserName.addAll(oldRelationUsers);
        }
        if (!lstUserName.isEmpty()) {
            /*??????*/
            lstUserName = removeDuplicate(lstUserName);
            //???????????????????????????
            // ???????????????????????????
            permitRefreshTaskBp.addPermitRefreshTask(lstUserName);
        }
    }

    /**
     * sys_key ????????? permit_key
     * @param list
     * @return
     */
    private List<String> changeKey(List<String> list){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List <String> permissions = permitItemPositionMapper.getPermission(list);
        List<String> result = new ArrayList<>();
        for (String str : permissions) {
            //?????????????????????
            result.addAll(funcJsonTreeBp.concatData(str));
        }
        return result;
    }

    /**
     * ??????
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


