package com.example.tradeshowproj.zCatalystSDK

import com.zoho.catalyst.common.ResponseInfo
import com.zoho.catalyst.datastore.ZCatalystDataStore
import com.zoho.catalyst.datastore.ZCatalystRow
import com.zoho.catalyst.datastore.ZCatalystSelectQuery
import com.zoho.catalyst.datastore.ZCatalystTable
import com.zoho.catalyst.filestore.ZCatalystFileStore
import com.zoho.catalyst.org.ZCatalystUser
import com.zoho.catalyst.setup.ZCatalystApp
import com.example.tradeshowproj.entities.BulkJotResponse
import com.example.tradeshowproj.entities.JotNote
import com.example.tradeshowproj.entities.PagingInfo
import com.example.tradeshowproj.entities.Status
import com.zoho.catalyst.function.ZCatalystFunction
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ZCatalystConstants {


    object zTableIds {
        const val JotsTableId = 12392000000011074

        const val LinksTableId = 11585000000216013L
        const val LinksCollectionTableId = 11585000000118753
    }
}
typealias TABLE_IDS = ZCatalystConstants.zTableIds


class ZApiSDK(private val zCatalystApp: ZCatalystApp) {
     val instancefun: ZCatalystFunction = zCatalystApp.getFunctionInstance(12392000000024606)
    private val zFileStore: ZCatalystFileStore = zCatalystApp.getFileStoreInstance()
    private val zDataStore: ZCatalystDataStore = zCatalystApp.getDataStoreInstance()

    private val zJotsTable = zDataStore.getTableInstance(ZCatalystConstants.zTableIds.JotsTableId)

    private val zLinksTable = zDataStore.getTableInstance(ZCatalystConstants.zTableIds.LinksTableId)
    private val zLinkCollectionsTable =
        zDataStore.getTableInstance(ZCatalystConstants.zTableIds.LinksCollectionTableId)


//    fun getFunction(){
//        val body = HashMap<String, Any>()
//        body.put("name", "Deck 1")
//        body.put("description", "A test deck")
//        body.put("isPublic",true)
//        body.put("image","base64encodedimagehere")
//        instancefun.executePost(
//            hashMapOf(),
//            body,
//            success = {
//                println("Success")
//            },
//            failure = {
//                println("failure")
//            }
//        )
//    }

    fun getGETfunction(){
        instancefun.executeGet(
            hashMapOf(),
            success = {
                println("Success")
            },
            failure = {
                println("failure")
            }
        )
    }


    suspend fun fetchUserDetails(): ZCatalystUser? {
        return suspendCoroutine { cont ->
            zCatalystApp.getCurrentUser(
                success = { zUser ->
                    cont.resume(zUser)
                }, failure = {
                    cont.resume(null)
                }
            )
        }
    }

    fun getBulkJots(
        nextToken: String? = null,
        onSuccess: (BulkJotResponse) -> Unit,
        onFailure: () -> Unit
    ) {
        if (nextToken != null) {
            getBulkJotsWithToken(nextToken, onSuccess, onFailure)
        } else {
            getBulkJotsWithOutToken(onSuccess, onFailure)
        }
    }

    fun createNewJot(
        data: JotNote,
        onSuccess: (JotNote) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val row = data.getRowInstance(zJotsTable)
        row.create(
            success = {
                onSuccess(it.toJotNote())
            },
            failure = { exception ->
                onFailure(exception)
            }
        )
    }

    fun updateJot(
        data: JotNote,
        onSuccess: (JotNote) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val row = data.getRowInstance(zJotsTable)
        row.update(
            success = {
                onSuccess(it.toJotNote())
            },
            failure = { exception ->
                onFailure(exception)
            }
        )
    }

    fun deleteNote(
        id: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        zJotsTable.deleteRow(
            rowId = id,
            success = {
                onSuccess()
            },
            failure = { exception ->
                onFailure(exception)
            }
        )
    }


    private fun getBulkJotsWithToken(
        token: String,
        onSuccess: (BulkJotResponse) -> Unit,
        onFailure: () -> Unit
    ) {
        zJotsTable.getRows(
            nextToken = token,
            success = { data, info ->
                onSuccess(
                    BulkJotResponse(
                        data = data.toJotNotes(),
                        info = info.toPagingInfo(),
                        status = if (data.isNotEmpty()) Status(200, "Success") else Status(
                            204,
                            "Empty"
                        )
                    )
                )
            },
            failure = { exception ->
                onFailure()
            }
        )
    }

    private fun getBulkJotsWithOutToken(
        onSuccess: (BulkJotResponse) -> Unit,
        onFailure: () -> Unit
    ) {
        zJotsTable.getRows(
            maxRows = 100,
            success = { data, info ->
                onSuccess(
                    BulkJotResponse(
                        data = data.toJotNotes(),
                        info = info.toPagingInfo(),
                        status = if (data.isNotEmpty()) Status(200, "Success") else Status(
                            204,
                            "Empty"
                        )
                    )
                )
            },
            failure = { exception ->
                onFailure()
            }
        )
    }

    // Query
    fun deleteMultipleJots(id: List<Long>, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        zDataStore.execute(
            selectQuery = ZCatalystSelectQuery.Builder().build(),
            success = {
                onSuccess()
            },
            failure = {
                onFailure(it.getErrorMsg().orEmpty())
            }
        )
    }
}

internal fun ResponseInfo.toPagingInfo() = PagingInfo(
    maxKeys = this.maxKeys,
    nextToken = this.nextToken,
    hasMoreData = this.moreRecords,
    totalRecords = this.totalRecords ?: 0
)

internal fun List<ZCatalystRow>.toJotNotes(): List<JotNote> {
    return map { it.toJotNote() }
}

internal fun ZCatalystRow.toJotNote(): JotNote {
    return JotNote(
        id = id,
        creatorId = creatorId,
        title = getFieldValue("title") as String,
        note = getFieldValue("note") as String,
        createdTime = createdTime,
        updatedTime = modifiedTime,
        isDeleted = getFieldValue("is_deleted") as String == "true"
    )
}


internal fun JotNote.getRowInstance(table: ZCatalystTable): ZCatalystRow {
    val row = table.newRow()
    if (this.id != 0L)
        row.setColumnValue("ROWID", id)

    row.setColumnValue("title", title)
    row.setColumnValue("note", note)
    row.setColumnValue("is_deleted", isDeleted.toString())
    return row
}

//sealed class ResultStatus<out T> {
//    data class Success<out T>(val data: T) : ResultStatus<T>()
//    data class Error(val text: String, val code: Int) : ResultStatus<Nothing>()
//}

//object ZCatalystQuery {
//    fun deleteMultipleJots(ids: List<Long>): ZCatalystSelectQuery {
//        return buildZQuery {
//            selectAll()
//            from("notes")
//            where(column = "", comparator = ZCatalystUtil. Comparator.LIKE , value = "")
//        }
//    }
//}
//
//
////ZCatalyst DSL for building queries
//private fun buildZQuery(block: ZCatalystSelectQuery.Builder.() -> Unit): ZCatalystSelectQuery {
//    val builder = ZCatalystSelectQuery.Builder()
//    builder.block()
//    return builder.build()
//}