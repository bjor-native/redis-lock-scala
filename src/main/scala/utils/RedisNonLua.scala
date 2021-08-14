package utils

import com.redis.RedisClient
import com.redis.api.StringApi.NX

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object RedisNonLua {

  def acquireLock(connection: RedisClient, lock: String, lockValue: String): Boolean = {
    if (connection.set(lock, lockValue, NX, 20 second)) true
    else false
  }

  def releaseLock(connection: RedisClient, lock: String, lockValue: String): Unit = {
    connection.get(lock) match {
      case Some(value) if value == lockValue => connection.del(lock)
      case _ => // Perform your operations
    }
  }
}
