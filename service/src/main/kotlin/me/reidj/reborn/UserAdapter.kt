package me.reidj.reborn

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Aggregates.limit
import com.mongodb.client.model.Aggregates.project
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Sorts
import me.reidj.thepit.data.Stat
import me.reidj.thepit.top.PlayerTopEntry
import me.reidj.thepit.top.TopEntry
import me.reidj.thepit.uitl.UtilCristalix
import org.bson.Document
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.BulkGroupsPackage
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit


/**
 * @project : forest
 * @author : Рейдж
 **/
open class UserAdapter(dbUrl: String, dbName: String, collection: String): MongoAdapter<Stat>(dbUrl, collection, dbName) {

    open fun <V> makeRatingByField(fieldName: String, limit: Int): List<TopEntry<Stat, V>> {
        val entries = ArrayList<TopEntry<Stat, V>>()
        val future: CompletableFuture<List<TopEntry<Stat, V>>> = CompletableFuture<List<TopEntry<Stat, V>>>()

        val operations = listOf(
            project(
                Projections.fields(
                    Projections.include(fieldName),
                    Projections.include("uuid"),
                    Projections.exclude("_id")
                )
            ), Aggregates.sort(Sorts.descending(fieldName)),
            limit(limit)
        )

        data.aggregate(operations).forEach({ document: Document ->
            if (readDocument<Stat>(document) == null) {
                throw NullPointerException("Document is null")
            }
            entries.add(TopEntry(readDocument(document)!!, document[fieldName] as V))
        }) { _: Void?, throwable: Throwable? ->
            if (throwable != null) {
                future.completeExceptionally(throwable)
                return@forEach
            }
            future.complete(entries)
        }

        return future.get()
    }

    fun getTop(topType: String, limit: Int): List<PlayerTopEntry<Any>> {
        val entries = makeRatingByField<String>(topType, limit)
        val playerEntries = mutableListOf<PlayerTopEntry<Any>>()

        entries.forEach { it.key.let { stat -> playerEntries.add(PlayerTopEntry(stat, it.value)) } }

        try {
            val uuids = arrayListOf<UUID>()

            entries.forEach { uuids.add(it.key.uuid) }

            val map = ISocketClient.get()
                .writeAndAwaitResponse<BulkGroupsPackage>(BulkGroupsPackage(uuids))
                .get(5L, TimeUnit.SECONDS)
                .groups.associateBy { it.uuid }

            playerEntries.forEach {
                map[it.key.uuid]?.let {data ->
                    it.userName = data.username
                    it.displayName = UtilCristalix.createDisplayName(data)
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            playerEntries.forEach {
                it.userName = "ERROR"
                it.displayName = "ERROR"
            }
        }
        return playerEntries.map {
            PlayerTopEntry(it.key, it.value).also { new ->
                new.displayName = it.displayName
                new.userName = it.userName
            }
        }
    }
}