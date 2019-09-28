/**
 * 
 */
package com.sczp.order.jpa.search.current;

/**
 * @author Administrator
 * 
 */

import com.sczp.order.jpa.utils.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class DynamicSpecifications {
	
	private DynamicSpecifications() {}
	
    private static final ConversionService conversionService = new DefaultConversionService();

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> clazz) {
        return new Specification<T>() {
            @SuppressWarnings({ "rawtypes", "unchecked", "static-access", "incomplete-switch" })
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (filters!=null && !filters.isEmpty()) {
                    List<Predicate> andList = new ArrayList<>();
                    List<Predicate> orList = new ArrayList<>();
                    for (SearchFilter filter : filters) {
                        // nested path translate
                        String[] names = StringUtils.split(filter.getFieldName(), ".");
                        Path expression = root.get(names[0]);
                        for (int i = 1; i < names.length; i++) {
                            expression = expression.get(names[i]);
                        }

                        // convert value
                        Class attributeClass = expression.getJavaType();
                        if (!attributeClass.equals(String.class) && filter.getValue() instanceof String && conversionService.canConvert(String.class, attributeClass) && !filter.getOperator().IS.equals(filter.getOperator())) {
                            filter.setValue(conversionService.convert(filter.getValue(), attributeClass));
                        }

                        switch (filter.getOperator()) {
                        case EQ:
                            addList(filter.isUseAnd(), andList, orList, builder.equal(expression, filter.getValue()));
                            break;
                        case LIKE:
                            addList(filter.isUseAnd(), andList, orList, builder.like(expression, "%" + filter.getValue() + "%"));
                            break;
                        case RLIKE:
                            addList(filter.isUseAnd(), andList, orList, builder.like(expression, filter.getValue() + "%"));
                            break;
                        case LLIKE:
                            addList(filter.isUseAnd(), andList, orList, builder.like(expression, "%" + filter.getValue()));
                            break;
                        case GT:
                            addList(filter.isUseAnd(), andList, orList, builder.greaterThan(expression, (Comparable) filter.getValue()));
                            break;
                        case LT:
                            addList(filter.isUseAnd(), andList, orList, builder.lessThan(expression, (Comparable) filter.getValue()));
                            break;
                        case GTE:
                            addList(filter.isUseAnd(), andList, orList, builder.greaterThanOrEqualTo(expression, (Comparable) filter.getValue()));
                            break;
                        case LTE:
                            addList(filter.isUseAnd(), andList, orList, builder.lessThanOrEqualTo(expression, (Comparable) filter.getValue()));
                            break;
                        case IS:
                            if ("NULL".equalsIgnoreCase(filter.getValue().toString())) {
                                addList(filter.isUseAnd(), andList, orList, builder.isNull(expression));
                            } else {
                                addList(filter.isUseAnd(), andList, orList, builder.isNotNull(expression));
                            }
                            break;
                        case NEQ:
                            addList(filter.isUseAnd(), andList, orList, builder.notEqual(expression, (Comparable) filter.getValue()));
                            break;
                        case IN:
                            if (filter.getValue() instanceof Object[]) {
                                List<Object> ls = new ArrayList<>();
                                for (Object object : (Object[]) filter.getValue()) {
                                    ls.add(object);
                                }
                                addList(filter.isUseAnd(), andList, orList, in(builder, expression, ls));
                            } else if (filter.getValue() instanceof Collection) {
                                addList(filter.isUseAnd(), andList, orList, in(builder, expression, (Collection<Object>) filter.getValue()));
                            } else {
                                List<Object> ls = new ArrayList<>();
                                ls.add(filter.getValue());
                                addList(filter.isUseAnd(), andList, orList, in(builder, expression, ls));
                            }
                            break;
                        case NIN:
                            if (filter.getValue() instanceof Object[]) {
                                List<Object> ls = new ArrayList<>();
                                for (Object object : (Object[]) filter.getValue()) {
                                    ls.add(object);
                                }
                                addList(filter.isUseAnd(), andList, orList, builder.not(in(builder, expression, ls)));
                            } else if (filter.getValue() instanceof Collection) {
                                addList(filter.isUseAnd(), andList, orList, builder.not(in(builder, expression, (Collection<Object>) filter.getValue())));
                            } else {
                                List<Object> ls = new ArrayList<>();
                                ls.add(filter.getValue());
                                addList(filter.isUseAnd(), andList, orList, builder.not(in(builder, expression, ls)));
                            }
                        }
                    }
                    int osize = orList.size();
                    Predicate pre = null;
                    if (!andList.isEmpty()) {
                        pre = builder.and(andList.toArray(new Predicate[andList.size()]));
                    }
                    if (osize > 0) {
                        Predicate or = builder.or(orList.toArray(new Predicate[orList.size()]));
                        if (pre != null) {
                            return builder.and(pre, or);
                        } else {
                            return or;
                        }
                    }
                    if (pre != null) {
                        return pre;
                    }
                }
                return builder.conjunction();
            }

            /**
             * 根据条件放到And或Or的列表中.
             * 
             * @param useAnd
             * @param andList
             * @param orList
             * @param value
             */
            private void addList(boolean useAnd, List<Predicate> andList, List<Predicate> orList, Predicate value) {
                if (useAnd) {
                    andList.add(value);
                } else {
                    orList.add(value);
                }

            }
        };
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Predicate in(CriteriaBuilder builder, Path expression, Collection<Object> value) {
        if ((value == null) || (value.isEmpty())) {
            return null;
        }
        In in = builder.in(expression);
        for (Object object : value) {
            in.value(object);
        }
        return in;
    }

    /**
     * 创建动态查询条件组合.
     */
    public static <T> Specification<T> buildSpecification(Map<String, Object> searchParams, Class<T> clz) {
        List<SearchFilter> filters = SearchFilter.parseList(searchParams);
        return DynamicSpecifications.bySearchFilter(filters, clz);
    }
}
