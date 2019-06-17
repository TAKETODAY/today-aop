# TODAY AOP

## v1.0.0

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

## v1.0.2
- 修复空指针异常

## v1.0.4
- fix: add `AspectsDestroyListener` to clear `AspectsRegistry` aspects instance when context close 
- optimization code



## v1.0.5

- :bug: fix: Superclass has no null constructors but no arguments were given
- :bug: fix: #4 Aspects not created





