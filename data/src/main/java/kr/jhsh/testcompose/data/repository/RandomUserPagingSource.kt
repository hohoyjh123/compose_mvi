package kr.jhsh.testcompose.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kr.jhsh.testcompose.data.remote.api.RandomUserApi
import kr.jhsh.testcompose.data.remote.dto.RandomUserDto

/**
 * RandomUser.me API용 PagingSource
 * - Jetpack Paging 3 지원
 * - 페이지 기반 로딩 구현
 */
class RandomUserPagingSource(
    private val api: RandomUserApi
) : PagingSource<Int, RandomUserDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RandomUserDto> {
        return try {
            val page = params.key ?: STARTING_PAGE_INDEX
            val response = api.getUsers(page = page, results = params.loadSize)

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (response.results.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RandomUserDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}
