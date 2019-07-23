# TODAY AOP

:apple: today-aop is a high-performance lightweight aop framework 

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8ffb960eb2b04507977aeb409d51dea3)](https://www.codacy.com/app/TAKETODAY/today-aop?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=TAKETODAY/today-aop&amp;utm_campaign=Badge_Grade)

## 安装

```xml

<dependency>
    <groupId>cn.taketoday</groupId>
    <artifactId>today-aop</artifactId>
    <version>1.0.5.RELEASE</version>
</dependency>

```

## v1.0.5
- :bug: fix: Superclass has no null constructors but no arguments were given
- :bug: fix: #4 Aspects not created
- :bookmark: release v1.0.5 2019/7/24-1:54


## v1.0.4
- fix: add `AspectsDestroyListener` to clear `AspectsRegistry` aspects instance when context close 
- optimization code

## v1.0.2
- 修复空指针异常

## v1.0.1
- 优先使用注解标注joinPoint
- 优化代码

```java
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogAspect {

	@AfterReturning(Logger.class)
	public void afterReturning(@Returning Object returnValue) {
		log.debug("@AfterReturning returnValue: [{}]", returnValue);
	}
}
```

## v1.0.0
