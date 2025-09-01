package com.lexwilliam.transaction.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.model.dto.TransactionDto
import kotlinx.coroutines.tasks.await

class TransactionPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, Transaction>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Transaction>): QuerySnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, Transaction> {
        return try {
            val currentPage = params.key ?: query.limit(params.loadSize.toLong()).get().await()
            if (currentPage.isEmpty) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
            val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
            val nextPage =
                query.startAfter(
                    lastVisibleProduct
                ).limit(params.loadSize.toLong()).get().await()
            LoadResult.Page(
                data = currentPage.toObjects(TransactionDto::class.java).map { it.toDomain() },
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}