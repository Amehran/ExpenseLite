package com.amehran.expenselite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

interface TaskRepository {
    fun getTasks(): Flow<List<String>>

    suspend fun refresh()
}

@Singleton
class DummyTaskRepository
@Inject
constructor() : TaskRepository {
    override fun getTasks(): Flow<List<String>> = flowOf(emptyList())

    override suspend fun refresh() {
        // Do nothing for now
    }
}
