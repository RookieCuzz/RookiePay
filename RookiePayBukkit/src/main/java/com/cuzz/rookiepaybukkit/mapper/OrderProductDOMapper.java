package com.cuzz.rookiepaybukkit.mapper;

import com.cuzz.rookiepaybukkit.model.doo.OrderProductDO;
import com.cuzz.rookiepaybukkit.model.doo.OrderProductDOExample;
import com.cuzz.rookiepaybukkit.model.doo.OrderProductDOKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderProductDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    long countByExample(OrderProductDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    int deleteByExample(OrderProductDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    int deleteByPrimaryKey(OrderProductDOKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    int insert(OrderProductDO row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    int insertSelective(OrderProductDO row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    List<OrderProductDO> selectByExample(OrderProductDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    OrderProductDO selectByPrimaryKey(OrderProductDOKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    int updateByExampleSelective(@Param("row") OrderProductDO row, @Param("example") OrderProductDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    int updateByExample(@Param("row") OrderProductDO row, @Param("example") OrderProductDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    int updateByPrimaryKeySelective(OrderProductDO row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table order_products
     *
     * @mbg.generated Mon Dec 16 09:37:23 CST 2024
     */
    int updateByPrimaryKey(OrderProductDO row);
}