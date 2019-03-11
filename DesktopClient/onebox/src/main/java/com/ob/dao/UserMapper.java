package com.ob.dao;

import com.ob.model.User;
import com.ob.model.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper
 * @author
 */
public interface UserMapper {


    /**
     * 查询全部
     * @param example
     * @return
     */
    int countByExample(UserExample example);

    /**
     * 删除数据
     * @param example
     * @return
     */
    int deleteByExample(UserExample example);

    /**
     * 根据ID删除数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 添加用户信息
     * @param record
     * @return
     */
    int insert(User record);

    /**
     *
     * @param record
     * @return
     */
    int insertSelective(User record);

    /**
     * 根据条件查询对象信息
     * @param example
     * @return
     */
    List<User> selectByExample(UserExample example);

    /**
     * 根据ID查询用户对象
     * @param id
     * @return
     */
    User selectByPrimaryKey(Integer id);

    /**
     * 修改用户信息
     * @param record
     * @param example
     * @return
     */
    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    /**
     * 根据Example对象修改用户信息
     * @param record
     * @param example
     * @return
     */
    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    /**
     * 安逐渐选择更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * 安主键更新
     * @param record
     * @return
     */
    int updateByPrimaryKey(User record);
}