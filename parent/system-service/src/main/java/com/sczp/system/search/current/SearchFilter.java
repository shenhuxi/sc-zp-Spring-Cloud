package com.sczp.system.search.current;

import com.sczp.system.util.ReflectionUtils;
import com.sczp.system.util.StringUtils;
import org.springframework.data.domain.Sort.Order;

import java.util.*;
import java.util.Map.Entry;


public class SearchFilter {
    public enum Operator {
        EQ, LIKE, RLIKE, LLIKE, GT, LT, GTE, LTE, NEQ, IS, IN, NIN, OR
    }

    /**
     * 根据operator返回对应的比较规则字符串 
     * 
     * @param operator
     * @return
     */
    public static String getOperatorStr(Operator operator) {
        switch (operator) {
        case EQ:
            return QLFinder.EQ;
        case LIKE:
            return QLFinder.LIKE;
        case RLIKE:
            return QLFinder.LIKE;
        case LLIKE:
            return QLFinder.LIKE;
        case GT:
            return QLFinder.GT;
        case LT:
            return QLFinder.LT;
        case GTE:
            return QLFinder.GTE;
        case LTE:
            return QLFinder.LTE;
        case IS:
            return QLFinder.IS;
        case NEQ:
            return QLFinder.NEQ;
        case IN:
            return QLFinder.IN;
        case NIN:
            return QLFinder.NIN;
        case OR:
            return QLFinder.OR;
        }
        return QLFinder.LIKE;
    }

    /**
     * 属性数据类型
     */
    public enum PropertyType {
        S(String.class), I(Integer.class), L(Long.class), N(Double.class), D(Date.class), B(Boolean.class), SS(String[].class), IS(Integer[].class), LS(Long[].class), NS(Double[].class), DS(Date[].class), BS(Boolean[].class);

        private Class<?> clazz;

        PropertyType(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getValue() {
            return clazz;
        }
    }

    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 参数名
     */
    private String fieldNameTemp;
    /**
     * 字段值
     */
    private Object value;
    /**
     * 操作符
     */
    private Operator operator;

    /**
     * 是否使用And连接各条件
     */
    private boolean useAnd = true;

    
    
    public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldNameTemp() {
		return fieldNameTemp;
	}

	public void setFieldNameTemp(String fieldNameTemp) {
		this.fieldNameTemp = fieldNameTemp;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public boolean isUseAnd() {
		return useAnd;
	}

	public void setUseAnd(boolean useAnd) {
		this.useAnd = useAnd;
	}

	/**
     * 
     * @param fieldName
     *            字段名
     * @param operator
     *            操作符
     * @param value
     *            值
     */
    public SearchFilter(String fieldName, Operator operator, Object value) {
        this(fieldName, operator, value, true);
    }

    public SearchFilter(String fieldName, Operator operator, Object value, boolean useAnd) {
        this.fieldName = fieldName;
        this.fieldNameTemp = fieldName;
        this.value = value;
        this.operator = operator;
        this.useAnd = useAnd;
    }


    /**
     * 转换条件，把字符串的查询条件，封装为对象,
     * <p>
     * EQ_column1_S_AND 后面两个可以省略
     * <p>
     * 2016.11 修复BUG，同一字段，多个条件，如大于XXX and 小于 xxx，采用字段名做为key 会少掉一个条件。
     * <p>
     * 为了兼容原来的情况,可采用 getSearchFilter(Map<String, SearchFilter> filters,String name)
     * 方法来获得值
     * 
     * @param filterParams
     * @return 返回的Map中，Key与filterParams中的key一致
     */
    public static Map<String, SearchFilter> parse(Map<String, Object> filterParams) {
        Map<String, SearchFilter> filters = new LinkedHashMap<>();
        for (Entry<String, Object> entry : filterParams.entrySet()) {
            String key = entry.getKey();
            String[] names = key.split("_");
            if (names.length < 2) {
                throw new IllegalArgumentException(key + " is not a valid search filter name");
            }
            Object value = entry.getValue();
            if (value == null) {
                /*
                 * value 为null时不当做查询条件 add by chaihu
                 */
                continue;
            }
            boolean useAnd = isUseAnd(names);
            // 把数组对象转换成集合
            if (Operator.IN.name().equals(names[0]) || Operator.NIN.name().equals(names[0])) {
                if (value instanceof String) {
                    value = String.valueOf(value).split(",");
                }
                if (value instanceof Object[]) {
                    List<Object> ls = new ArrayList<>();
                    int len = ((Object[]) value).length;
                    for (int i = 0; i < len; i++) {
                        Object object = convertValue(names, ((Object[]) value)[i]);
                        ls.add(object);
                    }
                    value = ls;
                } else if (!(value instanceof Collection)) {
                    List<Object> ls = new ArrayList<>();
                    if (value instanceof int[]) {
                        for (Object object : (int[]) value) {
                            ls.add(object);
                        }
                    } else if (value instanceof long[]) {
                        for (Object object : (long[]) value) {
                            ls.add(object);
                        }
                    } else if (value instanceof short[]) {
                        for (Object object : (short[]) value) {
                            ls.add(object);
                        }
                    } else if (value instanceof float[]) {
                        for (Object object : (float[]) value) {
                            ls.add(object);
                        }
                    } else if (value instanceof double[]) {
                        for (Object object : (double[]) value) {
                            ls.add(object);
                        }
                    } else if (value instanceof char[]) {
                        for (Object object : (char[]) value) {
                            ls.add(object);
                        }
                    } else if (value instanceof boolean[]) {
                        for (Object object : (boolean[]) value) {
                            ls.add(object);
                        }
                    } else if (value instanceof byte[]) {
                        for (Object object : (byte[]) value) {
                            ls.add(object);
                        }
                    } else {
                        ls.add(value);
                    }
                    value = ls;
                }
            } else {
                /**
                 * 描述: 值为空时不添加查询条件 add by chaihu
                 */
                if (value instanceof String) {
                    if(String.valueOf(value).length() == 0){
                        continue;
                    }
                }
                value = convertValue(names, value);
                if (value instanceof Object[]) {
                    value = Arrays.asList(value);
                }
            }
            /**
             * 解决查询时因时间类型不匹配引起的无法使用索引的问题。
             */
            if (value instanceof Date) {
                value = new java.sql.Timestamp(((Date) value).getTime());
            }
            SearchFilter filter = new SearchFilter(names[1], Operator.valueOf(names[0]), value, useAnd);
            filters.put(key, filter);
        }
        return filters;
    }

    /**
     * 通过SearchFilter.fieldName 找到SearchFilter
     * 
     * @param filters
     * @param fieldName
     * @return
     */
    public static SearchFilter getSearchFilter(Map<String, SearchFilter> filters, String fieldName) {
        if (filters == null || StringUtils.isBlank(fieldName)) {
            return null;
        }
        for (Entry<String, SearchFilter> entry : filters.entrySet()) {
            if (fieldName.equals(entry.getValue().fieldName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 判断查询条件是否为AND
     * <p>
     * EQ_column1_S_AND 后面两个可以省略
     * <p>
     * EQ_column1_OR
     *
     * @param names
     * @return
     */
    public static boolean isUseAnd(String[] names) {
        if (names == null) {
            return true;
        }
        if (names.length == 3) {
            if (QLFinder.AND.equalsIgnoreCase(names[2])) {
                return true;
            } else if (QLFinder.OR.equalsIgnoreCase(names[2])) {
                return false;
            }
        } else if (names.length > 3 && QLFinder.OR.equalsIgnoreCase(names[3])) {
        	return false;
        }
        return true;
    }

    /**
     * 转换值的条件
     *
     * @param names
     * @param val
     * @return
     */
    public static Object convertValue(String[] names, Object val) {
        if (val == null) {
            return null;
        }
        if (names.length > 2 && !StringUtils.isBlank(names[2]) && !QLFinder.AND.equalsIgnoreCase(names[2]) && !QLFinder.OR.equalsIgnoreCase(names[2])) {
            names = splitKeys(names);
            Class<?> propertyType = Enum.valueOf(PropertyType.class, names[2]).getValue();
            if ("LTE".equals(names[0]) && Date.class.isAssignableFrom(propertyType) && (val instanceof String)) {
                // 如果没有时间,则自动加上时间
                String v = String.valueOf(val);
                if (v.indexOf(':') < 0) {
                    val = v + " 23:59:59";// 条件判断,如果没有时间,则自动加上时间
                }
            }
            return ReflectionUtils.convertValue(val, propertyType);
        }
        return val;
    }

    public static String[] splitKey(String key) {
        if (key == null || "".equals(key)) {
            return new String[0];
        }
        return splitKeys(key.split("_"));
    }

    public static String[] splitKeys(String[] keys) {
        if (keys == null || keys.length < 2) {
            return keys;
        }
        if ("".equals(keys[1])) {
            keys[1] = String.format("_%s", keys[2]);
            if (keys.length > 3) {
                keys[2] = keys[3];
            } else {
                keys[2] = "S";
            }
        }
        return keys;
    }

    /**
     * 转换查询条件
     *
     * @param filterParams
     * @return
     */
    public static List<SearchFilter> parseList(Map<String, Object> filterParams) {
        List<SearchFilter> filters = new ArrayList<>();
        Map<String, SearchFilter> maps = parse(filterParams);
        if (maps != null && !maps.isEmpty()) {
            for (Entry<String, SearchFilter> entry : maps.entrySet()) {
                filters.add(entry.getValue());
            }
        }
        return filters;
    }

    /**
     * 用于一个属性要赋多个值，如：查询时的开始时间和结束时间
     *
     * @param filterParams
     * @return
     */
    public static List<Map<String, SearchFilter>> parseListMap(Map<String, Object> filterParams) {
        List<Map<String, SearchFilter>> listMap = new ArrayList<>();
        int count = 0;// 如果一个属性要赋多个值，防止重复。
        for (Entry<String, Object> entry : filterParams.entrySet()) {
            String[] names = StringUtils.split(entry.getKey(), "_");
            if (names.length < 2) {
                throw new IllegalArgumentException(entry.getKey() + " is not a valid search filter name");
            }
            Object value = convertValue(names, entry.getValue());
            SearchFilter filter = new SearchFilter(names[1], Operator.valueOf(names[0]), value);
            Map<String, SearchFilter> filters = new LinkedHashMap<>();
            filters.put(filter.fieldName + count, filter);
            listMap.add(filters);
            count++;
        }

        return listMap;
    }

    /**
     * 把searchParams的过滤项填充到QLFinder中
     *
     * @param qlFinder
     * @param searchParams
     * @return
     */
    public static QLFinder fillFilterToQLFinder(QLFinder qlFinder, Map<String, Object> searchParams) {
        if (searchParams != null && !searchParams.isEmpty()) {
            Map<String, Object> andParams = new LinkedHashMap<>();// 填写AND条件
            Map<String, Object> orParams = new SearchOrMap();// 填写Or条件
            for (Entry<String, Object> entry : searchParams.entrySet()) {
                String key = entry.getKey();
                String[] names = StringUtils.split(key, "_");
                if (isUseAnd(names)) {
                    andParams.put(key, entry.getValue());
                } else {
                    orParams.put(key, entry.getValue());
                }
            }
            if (!orParams.isEmpty()) {
                andParams.put("OR_c", orParams);
            }
            return fillQLFinder(qlFinder, andParams, 1, null);
        }
        return qlFinder;
    }



    /**
     * 把searchParams的过滤项按指定的拼接方式type拼接填充到QLFinder中
     * 
     * @param qlFinder
     *            【QL查询对象】
     * @param searchParams
     *            【需要拼接的条件集合】
     * @param type
     *            【拼接方式：1表示and、2表示or】
     * @return
     */
    private static QLFinder fillQLFinder(QLFinder qlFinder, Map<String, Object> searchParams, int type, String tempparam) {
        List<SearchFilter> filters = SearchFilter.parseList(searchParams);
        if (filters != null && !filters.isEmpty()) {
            int j = 0;
            for (SearchFilter filter : filters) {
                String param = "QLFINDER_PARAM" + (j + 1);
                if (type == 2) {
                    param = tempparam + (j + 1);
                }
                StringBuilder sb = new StringBuilder();
                if (type == 1) {
                    if (!QLFinder.OR.equals(SearchFilter.getOperatorStr(filter.operator))) {
                        sb.append(QLFinder.WHERE_AND);
                    }
                } else if (type == 2) {
                    if (j == 0) {
                        sb.append(QLFinder.WHERE_AND).append(QLFinder.WHERE_L_BRACKETS);
                    } else {
                        sb.append(QLFinder.WHERE_OR);
                    }
                }
                if (QLFinder.IS.equals(SearchFilter.getOperatorStr(filter.operator))) {
                    sb.append(qlFinder.isNativeSQL() ? StringUtils.humpToUnderline(filter.fieldName) : filter.fieldName).append(QLFinder.WHERE_IS);
                    if (filter.value != null && "NOTNULL".equalsIgnoreCase(filter.value.toString())) {
                        sb.append(QLFinder.WHERE_NOTNULL);
                    } else {
                        sb.append(QLFinder.WHERE_NULL);
                    }
                } else if (QLFinder.OR.equals(SearchFilter.getOperatorStr(filter.operator))) {
                    if (filter.value instanceof SearchOrMap) {
                        qlFinder = fillQLFinder(qlFinder, (SearchOrMap) filter.value, 2, param);
                    } else {
                        throw new RuntimeException("OR运算符必须传入一个SearchOrMap类型的值。");
                    }
                } else {
                    sb.append(qlFinder.isNativeSQL() ? StringUtils.humpToUnderline(filter.fieldName) : filter.fieldName)
                    .append(" ").append(SearchFilter.getOperatorStr(filter.operator)).append(" :").append(param);
                }

                if (type == 2 && j == (filters.size() - 1)) {
                    sb.append(QLFinder.WHERE_R_BRACKETS);
                }
                if (QLFinder.IS.equals(SearchFilter.getOperatorStr(filter.operator))) {
                    qlFinder.append(sb.toString());
                } else if (!QLFinder.OR.equals(SearchFilter.getOperatorStr(filter.operator))) {
                    filter.fieldNameTemp = param;
                    qlFinder.append(sb.toString(), filter);
                }
                j++;
            }
        }
        return qlFinder;
    }

    /**
     * 把orders中的排序条件填充到qlFinder中
     * 
     * @param qlFinder
     * @param orders
     * @return
     */
    public static QLFinder fillFilterToQLFinder(QLFinder qlFinder, Order... orders) {
        return qlFinder.append(getOrderbySQL(orders));
    }

    /**
     * 根据排序对象数组获取排序SQL语句
     * 
     * @param orders
     * @return
     */
    public static String getOrderbySQL(Order... orders) {
        String sqlconn = "";
        if (orders != null && orders.length > 0) {
            boolean ok = false;
            StringBuilder str = new StringBuilder();
            for (Order order : orders) {
                if (ok && order != null) {
                    str.append(',');
                }
                if (order != null) {
                    ok = true;
                    str.append(order.getProperty()).append(" ").append(order.getDirection().toString());
                }
            }
            if (!"".equals(str.toString().trim())) {
                str.insert(0, " order by ");
                return str.toString();
            }
        }
        return sqlconn;
    }

    
 
}
