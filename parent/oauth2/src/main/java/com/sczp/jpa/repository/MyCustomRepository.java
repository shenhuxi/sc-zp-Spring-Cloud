package com.sczp.jpa.repository;


import com.sczp.jpa.former.MyResultTransformer;
import com.sczp.jpa.search.current.DynamicSpecifications;
import com.sczp.jpa.search.current.QLFinder;
import com.sczp.jpa.search.current.SearchFilter;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义repository的方法接口实现类,该类主要提供自定义的公用方法
 * @author dicky
 * @param <T>
 * @param <ID>
 */
public class MyCustomRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, Serializable>
		implements BaseRepository<T, Serializable> {

	private static final Logger LOG = LoggerFactory.getLogger(MyCustomRepository.class);

	private final EntityManager entityManager;

	public MyCustomRepository(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.entityManager = em;
	}

	public MyCustomRepository(final JpaEntityInformation<T, ID> entityInformation, final EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	public Page<T> findPageByParams(Map<String, Object> searchParams, PageRequest pageRequest) {
		return findAll(DynamicSpecifications.buildSpecification(searchParams, getDomainClass()), pageRequest);
	}

	@Override
	public List<T> findListByParams(Map<String, Object> searchParams, Sort sort) {
		return findAll(DynamicSpecifications.buildSpecification(searchParams, getDomainClass()),sort);
	}

	@Override
	public List<T> findListByParams(Map<String, Object> searchParams) {
		return findAll(DynamicSpecifications.buildSpecification(searchParams, getDomainClass()),
				new Sort(Sort.Direction.ASC, "id"));
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Page<T> findPageByNativeSQL(String sql, Class clz, PageRequest pageRequest) {
		return findPageByNativeSQLAndParams(sql, new HashMap<>(), clz, pageRequest);
	}


	@Override
	public <K> Page<K> findPageByJPQL(String jpql, Map<String, Object> searchParams, Class<K> clz, PageRequest pageRequest) {
		QLFinder qlFinder = new QLFinder(jpql, false);
		qlFinder = SearchFilter.fillFilterToQLFinder(qlFinder, searchParams);
		qlFinder = SearchFilter.fillFilterToQLFinder(qlFinder, this.getOrders(pageRequest.getSort()));
		return findPageByQLFinder(qlFinder, pageRequest, clz);
	}
	@Override
	public <K>List<K>  findListByJPQL(String jpql, Map<String, Object> searchParams,Class<K> clz, Sort sort) {
		QLFinder qlFinder = new QLFinder(jpql, false);
		qlFinder = SearchFilter.fillFilterToQLFinder(qlFinder, searchParams);
		qlFinder = SearchFilter.fillFilterToQLFinder(qlFinder, this.getOrders(sort));
		return findListByQLFinder(qlFinder, clz);
	}


	@Override
	@SuppressWarnings("rawtypes")
	public <K> Page<K> findPageByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz,
                                                    PageRequest pageRequest) {
		QLFinder qlFinder = new QLFinder(sql, true);
		qlFinder = SearchFilter.fillFilterToQLFinder(qlFinder, searchParams);
		qlFinder = SearchFilter.fillFilterToQLFinder(qlFinder, this.getOrders(pageRequest.getSort()));
		return findPageByQLFinder(qlFinder, pageRequest, clz);
	}
	@SuppressWarnings("rawtypes")
	@Override
	public <K>List<K> findListByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz, Sort sort) {
		QLFinder qlFinder = new QLFinder(sql, true);
		qlFinder = SearchFilter.fillFilterToQLFinder(qlFinder, searchParams);
		qlFinder = SearchFilter.fillFilterToQLFinder(qlFinder, this.getOrders(sort));
		return findListByQLFinder(qlFinder, clz);
	}


	/**
	 * 描述:分页
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <K> Page<K> findPageByQLFinder(QLFinder qlFinder, PageRequest pageRequest, Class<K> clz) {
		long count = countQLResult(qlFinder, qlFinder.isNativeSQL());
		Query query;
		// 如果是原生SQL语句则判断是否采用属性复制的方式
		if (qlFinder.isNativeSQL()) {
			query = createQuery(qlFinder.getQL(), qlFinder.isNativeSQL(),null);
			query.unwrap(SQLQuery.class).setResultTransformer(new MyResultTransformer(clz));
		} else {
			query = createQuery(qlFinder.getQL(), qlFinder.isNativeSQL(), clz);
		}
		qlFinder.setParameterToQuery(query);
		setPageParameterToQuery(query, pageRequest);
		List<K>content = query.getResultList();
		return new PageImpl<>(content, pageRequest, count);
	}

	/**
	 * 描述:不分页
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <K>List<K> findListByQLFinder(QLFinder qlFinder, Class<K> clz) {
		Query query;
		// 如果是原生SQL语句则判断是否采用属性复制的方式
		if (qlFinder.isNativeSQL()) {
			query = createQuery(qlFinder.getQL(), qlFinder.isNativeSQL(),null);
			query.unwrap(SQLQuery.class).setResultTransformer(new MyResultTransformer(clz));
		} else {
			query = createQuery(qlFinder.getQL(), qlFinder.isNativeSQL(), clz);
		}
		qlFinder.setParameterToQuery(query);
		List<K> result = query.getResultList();
		entityManager.clear();// 分离内存中受EntityManager管理的实体bean，让VM进行垃圾回
		return result;
	}

	/**
	 * 设置分页参数到Query对象,辅助函数.
	 */
	private Query setPageParameterToQuery(final Query q, final Pageable pageable) {
		q.setFirstResult(pageable.getPageNumber());
		q.setMaxResults(pageable.getPageSize());
		return q;
	}

	/**
	 * 根据查询JPQL与参数列表创建Query对象.
	 */
	private Query createQuery(final String queryString, final boolean nativeSQL, Class clz) {
		Query query;
		if (nativeSQL) {
			if (clz == null) {
				query = entityManager.createNativeQuery(queryString);
			} else {
				query = entityManager.createNativeQuery(queryString, clz);
			}
		} else {
			query = entityManager.createQuery(queryString);
		}
		return query;
	}

	/**
	 * 通过count查询获得本次查询所能获得的对象总数.
	 * 
	 * @return
	 */
	private long countQLResult(final QLFinder qlFinder, final boolean nativeSQL) {
		Query query = createQuery(qlFinder.getRowCountQL(), nativeSQL,null);
		qlFinder.setParameterToQuery(query);
		try {
			Long count = 0L;
			if (nativeSQL) {// 原生SQL
				count = Long.parseLong(query.getSingleResult().toString());
			} else {
				count = (Long) query.getSingleResult();
			}
			return count;
		} catch (Exception e) {
			throw new RuntimeException("QL can't be auto count, QL is:" + qlFinder.getRowCountQL(), e);
		} finally {
			entityManager.clear();// 分离内存中受EntityManager管理的实体bean，让VM进行垃圾回收
		}
	}

	/**
	 * 创建排序
	 * 
	 * @param orders
	 * @return
	 */
	private Sort buildSort(Order... orders) {
		Sort sort = null;
		if (orders != null) {
			for (Order order : orders) {
				if (order != null) {
					if (sort == null) {
						sort = new Sort(order);
					} else {
						sort = sort.and(new Sort(order));
					}
				}
			}
		}
		return sort;
	}

	/**
	 * 获取排序
	 * 
	 * @param sort
	 * @return
	 */
	private Order[] getOrders(Sort sort) {
		if (sort != null) {
			List<Order> orderList = new ArrayList<>();
			if (sort != null) {
				for (Order order : sort) {
					orderList.add(order);
				}
			}
			if (!orderList.isEmpty()) {
				int size = orderList.size();
				Order[] orders = new Order[size];
				for (int i = 0; i < size; i++) {
					orders[i] = orderList.get(i);
				}
				return orders;
			}
		}

		return new Order[0];
	}

}