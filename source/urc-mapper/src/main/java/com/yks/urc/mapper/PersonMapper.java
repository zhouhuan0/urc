package com.yks.urc.mapper;

import java.util.List;

import com.yks.urc.entity.Person;
import com.yks.urc.vo.PersonVO;

public interface PersonMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Person record);

    int insertSelective(Person record);

    Person selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Person record);

    int updateByPrimaryKey(Person record);
    

	List<Person> getUserByDingOrgId(String dingOrgId);

	List<PersonVO> getUserByUserInfo(PersonVO personVO);

	/**
	 * 批量插入人员
	 * @param personList
	 */
	int insertBatchPerson(List<Person> personList);

	/**
	 * 清空所有的人员表
	 */
	void deleteAllPerson();
	
}