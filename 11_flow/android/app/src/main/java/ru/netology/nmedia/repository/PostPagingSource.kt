package ru.netology.nmedia.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import java.io.IOException

class PostPagingSource(
    private val apiService: ApiService
) : PagingSource<Long, Post>() {

    // метод для обновления данных
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    // мметод для принятия и обработки пагинации
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val result = when (params) {
                is LoadParams.Refresh -> {
                    apiService.getLatest(params.loadSize)
                }
                is LoadParams.Append -> {
                    apiService.getBefore(id = params.key, count = params.loadSize)
                }
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(), nextKey = null, prevKey = params.key,
                )
            }
            if (!result.isSuccessful) {
               // throw HttpException(result)
                Log.d("TAG", "${ApiError(result.code(), result.message())}")
                throw ApiError(result.code(), result.message())
            }
            val data = result.body().orEmpty()
            return LoadResult.Page(
                data = data,
                prevKey = params.key,
                nextKey = data.lastOrNull()?.id
            )
        } catch (e : IOException) {
            return  LoadResult.Error(e)
        }
    }
}