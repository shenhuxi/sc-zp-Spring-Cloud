package com.sczp.order.jpa.service.impl;

import com.sczp.order.jpa.exception.DataNotFoundException;
import com.sczp.order.jpa.former.MyResultTransformer;
import com.sczp.order.jpa.repository.BaseRepository;
import com.sczp.order.jpa.service.BaseService;
import com.sczp.order.jpa.utils.BeanUtil;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 *
 * Create By qinzhw
 * 2018年5月8日上午11:11:42
 */
public abstract class BaseServiceImpl<E,ID extends Serializable> implements BaseService<E,ID> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext
	protected EntityManager em;

	public abstract BaseRepository<E, ID> getCommonRepository();

	@Override
	public E get(ID id) {
		return getCommonRepository().getOne(id);
	}

	/**
	 * 根据ID查找某个Entity（建议使用）
	 * findOne:查询一个不存在的id数据时，返回的值是null.
	 * getOne:查询一个不存在的id数据时，直接抛出异常
	 */
	@Override
	public E findOne(ID id) throws DataNotFoundException {
		Optional<E> byId = getCommonRepository().findById(id);
        E e = byId.orElseThrow(() -> new DataNotFoundException("数据不存在"+id.toString()));
        return e;
	}

	@Override
	public List<E> getAll() {
		return getCommonRepository().findAll();
	}

	@Override
	public Long getTotalCount() {
		return getCommonRepository().count();
	}

	@Override
	public E save(E entity){
		return getCommonRepository().save(entity);
	}


	@Override
	public E update(ID id,E entity) {
        Optional<E> byId = getCommonRepository().findById(id);
        E one = byId.orElseThrow(() -> new RuntimeException(id.toString()));
		BeanUtil.copyPropertiesIgnoreNull(entity,one);
		return getCommonRepository().save(one);
	}

	@Override
	public void delete(E entity) {
		getCommonRepository().delete(entity);
	}

	@Override
	public void delete(ID id) {
		try {
			getCommonRepository().deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			//如果是ID在DB不存在,不往外抛异常 add shixh 0521
			logger.info("如果是ID在DB不存在,不往外抛异常");
		}
		
	}

	@Override
	public void delete(Collection<E> entities) {
		getCommonRepository().deleteAll(entities);
	}

	@Override
	public void flush() {
		getCommonRepository().flush();
	}

	@Override
	public List<E> findAll(Specification<E> spec) {
		return getCommonRepository().findAll(spec);
	}

	@Override
	public List<E> findAll(Map<String, Object> searchParams) {
		return getCommonRepository().findListByParams(searchParams);
	}

	@Override
	public List<E> findAll(Map<String, Object> searchParams, Sort sort) {
		return getCommonRepository().findListByParams(searchParams,sort);
	}

	@Override
	public Page<E> findAll(Map<String, Object> searchParams, PageRequest pageRequest){
		return getCommonRepository().findPageByParams(searchParams, pageRequest);
	}

	@Override
	public Page<E> findAll(Pageable pageable){
		return getCommonRepository().findAll(pageable);
	}

	@Override
	public Page<E> findAll(Specification<E> spec, Pageable pageable) {
		return getCommonRepository().findAll(spec, pageable);
	}

	@Override
	public List<E> findAll(Specification<E> spec, Sort sort) {
		return getCommonRepository().findAll(spec,sort);
	}
	
	/**
	 * 查询某个条件的结果数集
	 * @param spec
	 * @return
	 */
	@Override
	public long count(Specification<E> spec) {
		return getCommonRepository().count(spec);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Page<E> getPage(String sql, PageRequest pageable, Class clazz) {
		return getCommonRepository().findPageByNativeSQL(sql, clazz, pageable);
	}

	@Override
	public <K> Page<K> getPageVo(String sql, PageRequest pageable, Class<K> clazz){
		Query query = em.createNativeQuery(sql);
		query.setFirstResult(pageable.getPageNumber());
		query.setMaxResults(pageable.getPageSize());
		query.unwrap(SQLQuery.class)
				.setResultTransformer(new MyResultTransformer(clazz));
		List<K> content = query.getResultList();
		int count = content.size();
		PageImpl<K> page = new PageImpl<K>(content, pageable, count);
		return page;
	}
	@Override
	public <K> List<K> getListVo(String sql, Class<K> clazz){
		Query query = em.createNativeQuery(sql);
		query.unwrap(SQLQuery.class)
				.setResultTransformer(new MyResultTransformer(clazz));
		List<K> content = query.getResultList();
		return content;
	}

    @Override
    public <K> Page<K> findPageByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz, PageRequest pageRequest) {
        return getCommonRepository().findPageByNativeSQLAndParams(sql, searchParams, clz, pageRequest);
    }
    @Override
    public <K>List<K> findListByNativeSQLAndParams(String sql, Map<String, Object> searchParams, Class<K> clz, Sort sort) {
        return getCommonRepository().findListByNativeSQLAndParams(sql, searchParams, clz, sort);
    }


    @Override
    public <K> Page<K> findPageByJPQL(String jpql, Map<String, Object> searchParams, Class<K> clz, PageRequest pageRequest) {
        return getCommonRepository().findPageByJPQL(jpql, searchParams, clz, pageRequest);
    }
    @Override
    public <K>List<K> findListByJPQL(String jpql, Map<String, Object> searchParams, Class<K> clz, Sort sort) {
        return getCommonRepository().findListByJPQL(jpql, searchParams, clz, sort);
    }

}