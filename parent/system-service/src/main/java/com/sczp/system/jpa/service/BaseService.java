package com.sczp.system.jpa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 *
 * Create By qinzhw
 * 2018年5月8日上午11:11:42
 */
public interface BaseService<E,ID extends Serializable>  {

	/**
	 * 根据ID获取某个Entity
	 * @param id
	 * @return
	 */
	E get(ID id);

	/**
	 * 根据ID查找某个Entity（建议使用）
	 * @param id
	 * @return
	 */
	E findOne(ID id);

	/**
	 * 获取所有的Entity列表
	 * @return
	 */
	List<E> getAll();
	
	/**
	 * 获取Entity的总数
	 * @return
	 */
	Long getTotalCount();

	/**
	 * 保存Entity
	 * @param entity
	 * @return
	 */
	E save(E entity);

	
	/**
	 * 修改Entity
	 * @param entity
	 * @return
	 */
	E update(ID id, E entity);

	/**
	 * 删除Entity
	 * @param entity
	 */
	void delete(E entity);

	/**
	 * 根据Id删除某个Entity
	 * @param id
	 */
	void delete(ID id);

	/**
	 * 删除Entity的集合类
	 * @param entities
	 */
	void delete(Collection<E> entities);

	/**
	 * 清空缓存，提交持久化
	 */
	void flush();

	/**
	 * 根据查询信息获取某个Entity的列表
	 * @param spec
	 * @return
	 */
	List<E> findAll(Specification<E> spec);

	/**
	 * 根据查询信息获取某个Entity的列表
	 * @param searchParams
	 * @return
	 */
	List<E> findAll(Map<String, Object> searchParams);

	/**
	 * 根据查询信息获取某个Entity的列表
	 * @param searchParams
	 * @return
	 */
	List<E> findAll(Map<String, Object> searchParams, Sort sort);

	/**
	 * 获取Entity的分页信息
	 * @param searchParams
	 * @param pageRequest
	 * @return
	 */
	Page<E> findAll(Map<String, Object> searchParams, PageRequest pageRequest);

	/**
	 * 获取Entity的分页信息
	 * @param pageable
	 * @return
	 */
	Page<E> findAll(Pageable pageable);

	/**
	 * 根据查询条件和分页信息获取某个结果的分页信息
	 * @param spec
	 * @param pageable
	 * @return
	 */
	Page<E> findAll(Specification<E> spec, Pageable pageable);

	/**
	 * 根据查询条件和排序条件获取某个结果集列表
	 * @param spec
	 * @param sort
	 * @return
	 */
	List<E> findAll(Specification<E> spec, Sort sort);

	/**
	 * 查询某个条件的结果数集
	 * @param spec
	 * @return
	 */
	long count(Specification<E> spec);

	/**
	 * 支持多个表字段 返回 一个实体对象
	 * @author shixh
	 * @param sql
	 * @param pageable
	 * @param clazz 返回对象类型
	 * @return
	 */
	@SuppressWarnings("rawtypes")
    Page<E> getPage(String sql, PageRequest pageable, Class clazz);


	/**
	 * 原生sql返回自定义Page<VO>对象
	 * @param sql
	 * @param pageable
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	<T> Page<T> getPageVo(String sql, PageRequest pageable, Class<T> clazz);

	/**
	 * 原生sql返回自定义List<VO>对象
	 * @param sql
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	<T> List<T> getListVo(String sql, Class<T> clazz);


	/**
	 * 根据原生SQL及查询条件查询数据
	 * @param sql
	 * @param searchParams 查询条件
	 * @param clz 对象类名
	 * @param pageRequest 分页参数对象
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	<K> Page<K> findPageByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz, PageRequest pageRequest) ;

	/**
	 * 原生SQL和查询条件查询 不分页
	 * @param sql
	 * @param searchParams
	 * @param clz
	 * @param sort
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	<K>List<K> findListByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz, Sort sort);

	/**
	 * 按JQL分页查询
	 * @param jpql
	 * @param searchParams 【过滤条件】
	 * @param pageRequest 【分页参数对象】
	 * @return
	 */
	<K> Page<K> findPageByJPQL(String jpql, Map<String, Object> searchParams, Class<K> clz, PageRequest pageRequest);

	/**
	 * 描述: 按JQL分页查询 不分页
	 * 作者: qinzhw
	 * 创建时间: 2018/8/20 19:49
	 */
	<K>List<K> findListByJPQL(String jpql, Map<String, Object> searchParams, Class<K> clz, Sort sort);

}