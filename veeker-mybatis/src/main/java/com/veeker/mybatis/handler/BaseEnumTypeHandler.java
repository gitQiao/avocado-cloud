package com.veeker.mybatis.handler;

import com.veeker.core.enums.BaseEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *  数字枚举类型处理器
 *
 * @author ：qiaoliang
 * @date 2020-08-26 11:33
 */
public class BaseEnumTypeHandler<E extends Enum<E> & BaseEnum> extends BaseTypeHandler<E> {

    private final Class<E> clazz;

    private E[] enums;

    private Map<Object, E> enumMap;

    public BaseEnumTypeHandler(Class<E> clazz) {
        this.clazz = clazz;
        this.enums = clazz.getEnumConstants();
        enumMap = new HashMap<>(enums.length);
        for (E e : enums) {
            enumMap.put(Objects.toString(e.getValue()), e);
        }
    }


    /**
     * 用于定义设置参数时，该如何把Java类型的参数转换为对应的数据库类型；
     *
     * @param ps
     * @param i
     * @param parameter
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter,
                                    JdbcType jdbcType) throws SQLException {
        /*
         * BaseTypeHandler已经帮我们做了parameter的null判断
         * 数据库存储的是枚举的值，所以我们这里使用 value ， 如果需要存储 name，可以自定义修改
         */
        if (jdbcType == null) {
            ps.setObject(i, parameter.getValue());
        } else {
            ps.setObject(i, parameter.getValue(), jdbcType.TYPE_CODE);
        }
    }

    /**
     * 用于定义通过字段名称获取字段数据时，如何把数据库类型转换为对应的Java类型
     *
     * @param rs
     * @param columnName
     * @return
     * @throws SQLException
     */
    @Override
    public E getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String i = rs.getString(columnName);
        return rs.wasNull() ? null : enumMap.get(i);
    }

    /**
     * 用于定义通过字段索引获取字段数据时，如何把数据库类型转换为对应的Java类型；
     *
     * @param rs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String i = rs.getString(columnIndex);
        return rs.wasNull() ? null : enumMap.get(i);
    }

    /**
     * 用定义调用存储过程后，如何把数据库类型转换为对应的Java类型
     *
     * @param cs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String i = cs.getString(columnIndex);
        return cs.wasNull() ? null : enumMap.get(i);
    }

    private static <E extends Enum<?> & BaseEnum> E valueOf(Class<E> clazz, Object value) {
        E[] constants = clazz.getEnumConstants();
        for (E e : constants) {
            if (e.getValue().equals(value)){
                return e;
            }
        }
        return null;
    }

}
