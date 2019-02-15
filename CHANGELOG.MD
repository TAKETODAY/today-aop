# TODAY AOP

## v1.0.0.RELEASE

## v1.0.1.RELEASE
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

## v1.0.2.RELEASE
- 修复空指针异常

## v1.0.4.RELEASE
- fix: add `AspectsDestroyListener` to clear `AspectsRegistry` aspects instance when context close 
- optimization code

