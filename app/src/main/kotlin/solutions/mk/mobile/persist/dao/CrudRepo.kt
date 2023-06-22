package solutions.mk.mobile.persist.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface CrudRepo<E> {
    @Insert suspend fun insert(entity: E)
    @Update suspend fun update(entity: E)
    @Delete suspend fun delete(entity: E)

    @Insert suspend fun insertAll(entities: Iterable<E>)
    @Insert suspend fun insertAll(vararg entities: E)
    @Update suspend fun updateAll(entities: Iterable<E>)
    @Update suspend fun updateAll(vararg entities: E)
    @Delete suspend fun deleteAll(entities: Iterable<E>)
    @Delete suspend fun deleteAll(vararg entities: E)
}