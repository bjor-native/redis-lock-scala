import _root_.com.redis.RedisClient
import utils.RedisTool

import scala.language.postfixOps

object RedisLock extends App {
  def updateTableInfo(): Map[String, Any] = {
    var resMap = Map[String, Any]()
    val lockKey = "lock"
    //val requestId = UUID.randomUUID().toString().replace("-", "").toUpperCase()
    val flag = RedisTool.releaseDistributedLock(lockKey, "1")
    if (flag) {
      try {
        // Perform your operations
        val client = new RedisClient("localhost", 6379)
        client.get("gate") match {
          case Some(value) => println(value)
          case None        => println("None")
        }
        resMap = Map("code" -> 200, "msg" -> "success")
      } catch {
        case e: Exception =>
          e.printStackTrace()
          resMap = Map("code" -> 200101, "msg" -> "execution failure")
      }
    } else {
      resMap = Map(
        "code" -> 200102,
        "msg" -> "operation conflict, has been the first to be boarded by others."
      )
    }
    resMap
  }
}
