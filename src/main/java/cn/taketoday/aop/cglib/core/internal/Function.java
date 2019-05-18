package cn.taketoday.aop.cglib.core.internal;

public interface Function<K, V> {
    V apply(K key);
}
