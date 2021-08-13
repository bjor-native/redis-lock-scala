package utils

import _root_.com.redis.RedisClientPool

object CacheManager {
  //  val redisClientPool: RedisClientPool = "dev".equalsIgnoreCase(System.getenv("SCALA_ENV")) match {
  //    // Development environment
  //    case true => new RedisClientPool("127.0.0.1", 6379)
  //    // Other environments
  //    case false => new RedisClientPool("10.180.x.y", 6379, 8, 0, Some("root"))
  //  }
  val redisClientPool = new RedisClientPool("localhost", 6379)

  val redisDBNum = 10

  def getRedisKeyPrefix(isPersist: Boolean): String = {
    if (isPersist) {
      // Permanent cache prefix
      "persist_"
    } else {
      // Temporary cache prefix
      "tmp_"
    }
  }
}