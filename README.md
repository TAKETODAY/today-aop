# TODAY Aop

:apple: today-aop is a high-performance lightweight aop framework 

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8ffb960eb2b04507977aeb409d51dea3)](https://www.codacy.com/app/TAKETODAY/today-aop?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=TAKETODAY/today-aop&amp;utm_campaign=Badge_Grade)
![Java CI](https://github.com/TAKETODAY/today-aop/workflows/Java%20CI/badge.svg)

## 🛠️ 安装

```xml

<dependency>
    <groupId>cn.taketoday</groupId>
    <artifactId>today-aop</artifactId>
    <version>1.0.5.RELEASE</version>
</dependency>

```

- [Maven Central](https://search.maven.org/artifact/cn.taketoday/today-aop/1.0.5.RELEASE/jar)

## 案例
- [DEMO](https://github.com/TAKETODAY/today-web-demo)

## :memo: 使用说明

> 使用@Aspect标注一个切面

```java
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogAspect {

	@AfterReturning(Logger.class)
	public void afterReturning(@Returning Object returnValue) {
		log.debug("@AfterReturning returnValue: [{}]", returnValue);
	}

	@AfterThrowing(Logger.class)
	public void afterThrowing(@Throwing Throwable throwable) {
		log.error("@AfterThrowing With Msg: [{}]", throwable.getMessage(), throwable);
	}

	@Before(Logger.class)
	public void before(@Annotated Logger logger, @Argument User user) {
		log.debug("@Before method in class with logger: [{}] , Argument:[{}]", logger, user);
	}

	@After(Logger.class)
	public Object after(@Returning User returnValue, @Arguments Object[] arguments) {
		log.debug("@After method in class");
		return returnValue.setSex("女");
	}

	@Around(Logger.class)
	public Object around(@JoinPoint Joinpoint joinpoint) throws Throwable {
		log.debug("@Around Before method");
//		int i = 1 / 0;
		Object proceed = joinpoint.proceed();
		log.debug("@Around After method");
		return proceed;
	}
}

public @interface Logger {
	/** operation */
	String value() default "";
}

@Service
public class DefaultUserService implements UserService {

	@Autowired
	private UserDao userDao;

	@Logger("登录")
	@Override
	public User login(User user) {
		log.debug("login");
//		int i = 1 / 0;
		return userDao.login(user);
	}
	@Logger("注册")
	@Override
	public boolean register(User user) {
		return userDao.save(user);
	}
}
@Repository
public class UserDaoImpl implements UserDao {

	private Map<String, User> users = new HashMap<>();

	public UserDaoImpl() {
		users.put("666", new User(1, "杨海健", 20, "666", "666", "男", new Date()));
		users.put("6666", new User(2, "杨海健1", 20, "6666", "6666", "男", new Date()));
		users.put("66666", new User(3, "杨海健2", 20, "66666", "66666", "男", new Date()));
		users.put("666666", new User(4, "杨海健3", 20, "666666", "666666", "男", new Date()));
	}

	@Override
	public boolean save(User user) {
		users.put(user.getUserId(), user);
		return true;
	}
	@Override
	public User login(User user) {

		User user_ = users.get(user.getUserId());
		if (user_ == null) {
			return null;
		}
		if (!user_.getPasswd().equals(user.getPasswd())) {
			return null;
		}
		return user_;
	}
}

@Test
public void test_Login() throws NoSuchBeanDefinitionException {

	try (ApplicationContext applicationContext = new StandardApplicationContext("","")) {
		UserService bean = applicationContext.getBean(UserServiceImpl.class);
		User user = new User();
		user.setPasswd("666");
		user.setUserId("666");
		
		long start = System.currentTimeMillis();
		User login = bean.login(user);
		log.debug("{}ms", System.currentTimeMillis() - start);
		log.debug("Result:[{}]", login);
		log.debug("{}ms", System.currentTimeMillis() - start);
	}
}
```

### :memo: 开源协议

请查看 [GNU GENERAL PUBLIC LICENSE](https://github.com/TAKETODAY/today-aop/blob/master/LICENSE)

