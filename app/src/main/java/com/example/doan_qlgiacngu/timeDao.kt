package com.example.doan_qlgiacngu

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao

interface timeSleepDao {

    @Insert
    fun insert(timeSleep: timeSleep): Long

    @Update
    fun update(timeSleep: timeSleep)

    @Delete
    fun delete(timeSleep: timeSleep)

    @Query("SELECT * FROM timeSleep ORDER BY id DESC")
    fun getAll(): List<timeSleep>

    @Query("SELECT * FROM timeSleep WHERE id = :id")
    fun getById(id: Int): timeSleep?

    @Query("SELECT * FROM timeSleep ORDER BY id DESC LIMIT 1")
    fun getLast(): timeSleep

    @Query("""
    SELECT timeSleep.* FROM timeSleep
    INNER JOIN lienKet 
        ON timeSleep.id = lienKet.timeSleepId
    WHERE lienKet.userid = :userId
""")
    fun getTimeSleepByUser(userId: Int): List<timeSleep>
}


@Dao
interface timeAwakeDao {
    @Insert
    fun insert(t: timeAwake): Long
    @Update
    fun update(t: timeAwake)
    @Delete
    fun delete(t: timeAwake)
    @Query("SELECT * FROM timeAwake")
    fun getAll(): List<timeAwake>
    @Query("SELECT * FROM timeAwake WHERE id = :id")
    fun getById(id: Int): timeAwake

    @Query("SELECT * FROM timeAwake ORDER BY id DESC LIMIT 1")
    fun getLast(): timeAwake

    @Query("""
    SELECT timeAwake.* FROM timeAwake
    INNER JOIN lienKet 
        ON timeAwake.id = lienKet.timeAwakeId
    WHERE lienKet.userid = :userId
""")
    fun getTimeAwakeByUser(userId: Int): List<timeAwake>
}

@Dao
interface SleepExtraDao {
    @Query("SELECT * FROM sleep_extra WHERE dayId = :dayId")
    fun getByDayId(dayId: Int): SleepExtraEntity?

    @Insert
    fun insert(extra: SleepExtraEntity)
}

//timedao
@Dao
interface tgnguDao {
    @Insert
    fun insert(t: tgngu) : Long
    @Update
    fun update(t: tgngu)
    @Delete
    fun delete(t: tgngu)
    @Query("SELECT * FROM tgngu ORDER BY id DESC LIMIT 1")
    fun getLast(): tgngu?
    @Query("SELECT * FROM tgngu")
    fun getAll(): List<tgngu>

}

@Dao
interface lienKetDao {

    @Insert
    fun insert(lienKet: lienKet)

    @Delete
    fun delete(lienKet: lienKet)

    @Query("SELECT * FROM lienKet")
    fun getAll(): List<lienKet>

    @Query("SELECT * FROM lienKet WHERE userid = :userId")
    fun getByUser(userId: Int): lienKet?
}


@Dao
interface userDataDao {

    @Insert
    fun insert(user: userData): Long

    @Update
    fun update(user: userData)

    @Delete
    fun delete(user: userData)

    @Query("SELECT * FROM userData WHERE userid = :id")
    fun getById(id: Int): userData?

    @Query("SELECT * FROM userData WHERE email = :email")
    fun getByEmail(email: String): userData?

    @Query("SELECT * FROM userData WHERE login = 1 LIMIT 1")
    fun getCurrentUser(): userData?

    @Query("SELECT * FROM userData")
    fun getALL(): List<userData>
}

@Dao
interface muctieuDataDao {
    @Insert
    fun insert(muctieu: muctieuData)

    @Update
    fun update(muctieu: muctieuData)

    @Query("SELECT * FROM muctieuData WHERE userId = :userid")
    fun getMucTieu(userid: Int): muctieuData?

    @Query("SELECT * FROM muctieuData WHERE userId = :userid")
    fun getKiemTraMucTieu(userid: Int): Boolean
}


