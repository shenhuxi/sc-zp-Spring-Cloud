package com.sczp.system.service.impl;

import com.sczp.system.entity.User;
import com.sczp.system.jpa.repository.BaseRepository;
import com.sczp.system.jpa.service.impl.BaseServiceImpl;
import com.sczp.system.repository.UserRepository;
import com.sczp.system.service.UserService;
import com.sczp.system.util.threadPool.ThreadPoolInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
@Transactional
public 	class UserServiceImpl extends BaseServiceImpl<User,Long> implements UserService {
	private final RestTemplate restTemplate;
	private final RedisService redisService;
	private final RedisTemplate redisTemplate;

	private final UserRepository userRepository;
	private final ExecutorService threadPool = ThreadPoolInstance.getThreadPoolInstance();

	@Autowired
	public UserServiceImpl(RestTemplate restTemplate, RedisService redisService, RedisTemplate redisTemplate, UserRepository userRepository) {
		this.restTemplate = restTemplate;
		this.redisService = redisService;
		this.redisTemplate = redisTemplate;
		this.userRepository = userRepository;
	}

	//unless = "#result eq null"会出现缓存穿透哟
	@Cacheable(value = "user", key = "#name", unless = "#result eq null")
//	@CachePut
//	@CacheEvict
	public String getUserByName(String name) {
		//step1:查询缓存中是否有此人
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "{id:1,name:张三,age:25}";
	}


	/**
	 * 多线程下对锁的获取
	 * 	当获取锁失败，采用sleep  +自旋
	 * @param key key
	 * @return 200
	 */
	public String testRedisKey(String key) {
		for (int i = 1; i < 3; i++) {
			int finalI = i;
			threadPool.execute(() -> {
				System.out.println("循环:"+finalI+"由线程:"+Thread.currentThread().getName() +"前来获取锁！");
				while (true) {
					//step1.获取锁
					String uuid = UUID.randomUUID().toString();
					boolean b = redisService.setLock(key, uuid, 2000);
					if (b) {
						//step2处理逻辑 business.....
						System.out.println("循环 "+finalI+" 处理逻辑中....");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//step3.自己释放掉锁
						redisService.releaseLock(key, uuid);
						break;
					}else{
						System.out.println("**循环 "+finalI+" 获取锁失败....歇息100毫秒");
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				System.out.println("循环:"+finalI+"由线程:"+Thread.currentThread().getName() +"执行完毕");
			});
		}
		return "code:200";
	}

	@Override
	public String testSynchronized() {
		return null;
	}

	@Override
	public BaseRepository<User, Long> getCommonRepository() {
		return userRepository;
	}

	@Override
	public User findByUserName(String teacherName) {
		return userRepository.findByUserName(teacherName);
	}

}