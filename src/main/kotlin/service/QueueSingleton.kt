package er.codes.web.service

import er.codes.web.model.TimestampJson
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue

object QueueSingleton {
    // TODO: add micrometer metrics
    private val log = LoggerFactory.getLogger(QueueSingleton::class.java)
    private val queue = ConcurrentLinkedQueue<TimestampJson>()

    fun addToQueue(item: TimestampJson) {
        log.info("Adding $item to queue")
        queue.add(item)
    }

    fun getAllByClientId(clientId: String): List<TimestampJson> {
        log.info("Getting all items for clientId: $clientId")
        return queue.filter { it.clientId == clientId }
    }

    fun removeAllByClientId(clientId: String): List<TimestampJson> {
        log.info("Removing all items for clientId: $clientId")
        val removedItems = queue.filter { it.clientId == clientId }
        queue.removeAll(removedItems)
        return removedItems
    }

    fun pollFromQueue(): TimestampJson? {
        log.info("Polling from queue")
        return queue.poll()
    }

    fun getQueue(): List<TimestampJson> {
        log.info("Getting complete queue")
        return queue.toList()
    }

    fun clearQueue() {
        log.debug("Clearing queue")
        queue.clear()
    }

    fun isEmpty() = queue.isEmpty()

    fun size() = queue.size

}