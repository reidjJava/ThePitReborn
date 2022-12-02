package me.reidj.reborn

import com.mongodb.ClientSessionOptions
import com.mongodb.async.client.MongoClient
import com.mongodb.async.client.MongoClients
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.WriteModel
import com.mongodb.session.ClientSession
import me.reidj.thepit.data.Unique
import org.bson.Document
import ru.cristalix.core.GlobalSerializers
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
abstract class MongoAdapter<T : Unique>(dbUrl: String, collection: String, dbName: String) {

    private val mongoClient: MongoClient

    val session: ClientSession
    val data: MongoCollection<Document>
    val upsert: UpdateOptions = UpdateOptions().upsert(true)

    init {
        val future = CompletableFuture<ClientSession>()
        mongoClient = MongoClients.create(dbUrl).also {
            it.startSession(ClientSessionOptions.builder().causallyConsistent(true).build()) { response, throwable ->
                if (throwable != null) future.completeExceptionally(throwable) else future.complete(response)
            }
        }
        data = mongoClient.getDatabase(dbName).getCollection(collection)
        session = future.get(10, TimeUnit.SECONDS)
    }

    inline fun <reified T : Unique> find(uuid: UUID) = CompletableFuture<T?>().apply {
        data.find(session, Filters.eq("uuid", uuid.toString())).first { result: Document?, throwable: Throwable? ->
            throwable?.printStackTrace()
            complete(readDocument(result))
        }
    }

    inline fun <reified T : Unique> findAll(): CompletableFuture<Map<UUID, T>> {
        val future = CompletableFuture<Map<UUID, T>>()
        val documentFindIterable = data.find()
        val map = ConcurrentHashMap<UUID, T>()
        documentFindIterable.forEach({ document: Document ->
            val obj: T? = readDocument(document)
            if (obj != null) {
                map[obj.getUUID()] = obj
            }
        }) { _, throwable: Throwable? ->
            throwable?.printStackTrace()
            future.complete(map)
        }
        return future
    }

    inline fun <reified T : Unique> save(uniques: T) = save(listOf(uniques))

    inline fun <reified T : Unique> save(list: List<T>) {
        arrayListOf<WriteModel<Document>>().also { models ->
            list.forEach {
                models.add(
                    UpdateOneModel(
                        Filters.eq("uuid", it.getUUID().toString()),
                        Document("\$set", Document.parse(GlobalSerializers.toJson(it))),
                        upsert
                    )
                )
            }
            if (models.isNotEmpty()) {
                data.bulkWrite(session, models) { _, throwable: Throwable? -> throwable?.printStackTrace() }
            }
        }
    }

    fun clear(uuid: UUID) {
        data.deleteOne(Filters.eq("uuid", uuid.toString())) { _, throwable: Throwable? -> throwable?.printStackTrace() }
    }

    inline fun <reified T : Unique> readDocument(document: Document?) =
        if (document == null) null else GlobalSerializers.fromJson(document.toJson(), T::class.java)
}