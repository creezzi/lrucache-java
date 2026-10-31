# lrucache

A thread-safe, generic LRU cache with a compute-if-absent helper.

## Usage

```java
LruCache<String, User> cache = new LruCache<>(100);
cache.put("ada", user);
User u = cache.getOrCompute("bob", this::loadUser);
```

## Test

```bash
mvn test
```

## License

MIT
