package utils

import _root_.com.redis.api.StringApi.NX

import scala.concurrent.duration.Duration
import scala.language.postfixOps

/** redis distributed lock
  */
object RedisTool {

  val RELEASE_SUCCESS: String = "1"

  /** @ Param lock Key lock
    * @ Param requestId Request Identification
    * @ Param expireTime expiration time
    * @ Param isPersist temporary or permanent cache
    */
  def tryGetDistributedLock(
      lockKey: String,
      requestId: String,
      expireTime: Duration,
      isPersist: Boolean = false
  ): Unit = {
    CacheManager.redisClientPool.withClient(client => {
      //val redisKeyPrefix = CacheManager.getRedisKeyPrefix(isPersist)
      client.select(CacheManager.redisDBNum)
      val result = client.set(lockKey, requestId, NX, expireTime)
      var flag = false
      if (result) {
        flag = true
      }
      flag
    })
  }

  /** Release Distributed Locks
    * @ Param lock Key lock
    * @ Param requestId Request Identification
    * @ Param expireTime expiration time
    * @ Param isPersist temporary or permanent cache
    *
    * @return
    */
  def releaseDistributedLock(
      lockKey: String,
      requestId: String,
      expireTime: Int = 1,
      isPersist: Boolean = false
  ): Boolean = {
    CacheManager.redisClientPool.withClient(client => {
      val redisKeyPrefix = CacheManager.getRedisKeyPrefix(isPersist)
      //client.select(CacheManager.redisDBNum)
      // Lua script is also a singleton mode, which also ensures that only one thread executes the script at the same time.
      val lua =
        s"""
           |local current = redis.call('incrBy',KEYS[1],ARGV[1]);
           |if current == tonumber(ARGV[1]) then
           |  local t = redis.call('ttl',KEYS[1]);
           |  if t == -1 then
           |    redis.call('expire',KEYS[1],ARGV[2])
           |  end;
           |end;
           |return current;
      """.stripMargin
      val code = client.scriptLoad(lua).get
      val ret = client.evalSHA(
        code,
        List(redisKeyPrefix + lockKey),
        List(requestId, expireTime)
      )
      val result = ret.get.asInstanceOf[Object].toString
      var flag = false
      if (result == RELEASE_SUCCESS) {
        flag = true
        client.set("gate", Map(1 -> "success"))
      }
      flag
    })
  }
}
