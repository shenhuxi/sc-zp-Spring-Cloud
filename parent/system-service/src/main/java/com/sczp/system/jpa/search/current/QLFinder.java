package com.sczp.system.jpa.search.current;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * 
* Title: QLFinder 
* Description:  Query Language Finder 自定义查询语言
* @author dicky  
* @date 2018年7月5日 上午10:04:42
 */
public class QLFinder {

    public static final String ROW_COUNT_QL = "select count(*) ";
    public static final String FROM = "from";

    public static final String FROM_F = "FROM";
    public static final String DISTINCT = "distinct";
    public static final String QL_FETCH = "fetch";
    public static final String ORDER_BY = "order";

    public static final String AND = "and";
    public static final String OR = "or";
    public static final String LIKE = "like";
    public static final char LIKE_C = '%';
    public static final char QUESTION_C = '?';

    public static final String EQ = "=";
    public static final String GT = ">";
    public static final String LT = "<";
    public static final String GTE = ">=";
    public static final String LTE = "<=";
    public static final String IS = "is";
    public static final String NEQ = "<>";
    public static final String IN = "in";
    public static final String NIN = "not in";

    public static final String WHERE_NULL = " null ";
    public static final String WHERE_IS = " is ";
    public static final String WHERE_NOTNULL = " not null ";
    public static final String WHERE_AND = " and ";
    public static final String WHERE_OR = " or ";
    public static final String WHERE_L_BRACKETS = " (";
    public static final String WHERE_R_BRACKETS = ") ";

    private StringBuilder qlBuilder;

    private boolean nativeSQL = false;

    private List<SearchFilter> filters = new ArrayList<>();

    /** 构造函数 */
    public QLFinder(String ql, boolean nativeSQL) {
        qlBuilder = new StringBuilder(ql);
        this.nativeSQL = nativeSQL;
    }

    public static QLFinder create(String ql, boolean nativeSQL) {
        QLFinder hqlFinder = new QLFinder(ql, nativeSQL);
        return hqlFinder;
    }

    public QLFinder append(String ql) {
        qlBuilder.append(ql);
        return this;
    }

    public QLFinder append(String ql, SearchFilter filter) {
        qlBuilder.append(ql);
        filters.add(filter);
        return this;
    }

    /** 获取QL语句 */
    public String getQL() {
        return qlBuilder.toString();
    }
    /** 获取查询数据库记录数的QL语句 */
    public String getRowCountQL() {
        return wrap() + getFromAfter(getQL());
    }

    private String wrap() {
        return ROW_COUNT_QL;
    }
    public static String likeValue(SearchFilter filter) {
        if (filter == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (filter.getOperator() == SearchFilter.Operator.LIKE) {// 对LIKE查询条件的封装
            sb.append(LIKE_C).append(filter.getValue()).append(LIKE_C);
        } else if (filter.getOperator() == SearchFilter.Operator.RLIKE) {
            sb.append(filter.getValue()).append(LIKE_C);
        } else if (filter.getOperator() == SearchFilter.Operator.LLIKE) {
            sb.append(LIKE_C).append(filter.getValue());
        } else {
            sb.append(filter.getValue());
        }
        return sb.toString();
    }

    /**
     * 将HQLFinder中的参数设置到query中。
     * 
     * @param query
     */
    @SuppressWarnings("unchecked")
    public Query setParameterToQuery(Query query) {
        for (SearchFilter filter : filters) {
            if (filter.getOperator() == SearchFilter.Operator.LIKE ||
            		filter.getOperator() == SearchFilter.Operator.RLIKE ||
            		filter.getOperator() == SearchFilter.Operator.LLIKE) {// 对LIKE查询条件的封装
                query.setParameter(filter.getFieldNameTemp(), likeValue(filter));
            } else if (filter.getOperator() == SearchFilter.Operator.IN || filter.getOperator() == SearchFilter.Operator.NIN) {
                Collection<Object> value = new ArrayList<>();
                if (filter.getValue() instanceof Object[]) {
                    for (Object object : (Object[]) filter.getValue()) {
                        value.add(object);
                    }
                } else if (filter.getValue() instanceof Collection) {
                    value = (Collection<Object>) filter.getValue();
                } else {
                    value.add(filter.getValue());
                }
                query.setParameter(filter.getFieldNameTemp(), value);
            } else {
                query.setParameter(filter.getFieldNameTemp(), filter.getValue());
            }
        }
        return query;
    }

    public List<SearchFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<SearchFilter> filters) {
        this.filters = filters;
    }

    public boolean isNativeSQL() {
        return nativeSQL;
    }

    public void setNativeSQL(boolean nativeSQL) {
        this.nativeSQL = nativeSQL;
    }

    @Override
    public String toString() {
        return qlBuilder.toString();
    }

    /**
     * 截取sqlStr语句中from以后order by之前的字符串内容【包含from】
     *
     * @param sqlStr
     * @return
     */
    public static String getFromAfter(String sqlStr) {
        if (sqlStr==null||"".equals(sqlStr)) {
            return "";
        }
        // 把SQL语句变成全小写
        String sql = sqlStr.toLowerCase();
        // 去掉条件中的''符号
        sql = sql.replaceAll("''", "  ");

        // 去掉条件中字符串类型的常量值
        sql = replaceAllDyh(sql);

        // 去掉带括号的子查询
        sql = replaceAllKh(sql);

        int b = sql.lastIndexOf(" from ");
        int e = sql.lastIndexOf(" order ");
        if (b != -1) {
            if (e != -1) {
                return sqlStr.substring(b, e);
            } else {
                return sqlStr.substring(b, sqlStr.length());
            }
        } else {
            if (e != -1) {
                return sqlStr.substring(0, e);
            } else {
                return sqlStr;
            }
        }
    }
    /**
     * 把条件中的单引号''及引号中的内容替换成功空格
     *
     * @param sql
     * @return
     */
    private static String replaceAllDyh(String sql) {
    	 if (sql==null||"".equals(sql)) {
             return "";
         }
        String mysql = sql;
        int b = mysql.indexOf("'");
        if (b != -1) {
            mysql = mysql.substring(0, b) + " " + mysql.substring(b + 1, mysql.length());
        }
        int e = mysql.indexOf("'");
        if (e != -1) {
        	StringBuilder temp = new StringBuilder("");
            for (int i = b; i <= e; i++) {
                temp.append(" ") ;
            }
            mysql = mysql.substring(0, b) + temp + mysql.substring(e + 1, mysql.length());
        }
        if (mysql.indexOf("'") != -1) {
            mysql = replaceAllDyh(mysql);
        }
        return mysql;
    }
    /**
     * 把小括号()及括号中的内容替换成空格
     *
     * @param sqlStr
     * @return
     */
    private static String replaceAllKh(String sqlStr) {
    	 if (sqlStr==null||"".equals(sqlStr)) {
             return "";
         }
        String mysql = sqlStr;
        int b = mysql.lastIndexOf("(");
        int e = mysql.indexOf(")");
        String temp = mysql;
        while (b > e) {
            temp = temp.substring(0, e) + " " + temp.substring(e + 1, temp.length());
            e = temp.indexOf(")");
        }
        if (b != -1) {
            temp = "";
            for (int i = b; i <= e; i++) {
                temp += " ";
            }
            mysql = mysql.substring(0, b) + temp + mysql.substring(e + 1, mysql.length());
        }
        if (mysql.lastIndexOf("(") != -1) {
            mysql = replaceAllKh(mysql);
        }
        return mysql;
    }

}
