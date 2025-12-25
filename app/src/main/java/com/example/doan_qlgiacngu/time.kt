package com.example.doan_qlgiacngu

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey


@Entity(tableName = "timeSleep")
data class timeSleep(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var gio: String,
    var phut: String,
    var ngay: String,
    var thang: String,
    var nam: String
) {
    override fun toString(): String {
        return "${gio.padStart(2, '0')}:${phut.padStart(2, '0')} " +
                "${ngay.padStart(2, '0')}/${thang.padStart(2, '0')}/${nam}"
    }
}

@Entity(tableName = "timeAwake")
data class timeAwake(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var gio: String,
    var phut: String,
    var ngay: String,
    var thang: String,
    var nam: String
) {
    override fun toString(): String {
        return "${gio.padStart(2, '0')}:${phut.padStart(2, '0')} " +
                "${ngay.padStart(2, '0')}/${thang.padStart(2, '0')}/${nam}"
    }

}

@Entity(tableName = "sleep_extra")
data class SleepExtraEntity(
    @PrimaryKey
    val dayId: Int,        // = timeSleep.id
    val gioThayDoiTuThe: String,
    val gioThucDayNgan: String,
    val remManh: String,

    // Lưu biểu đồ dạng chuỗi: "10,20,30,..."
    val chartData: String
)

//time
@Entity(tableName = "tgngu")
data class tgngu(@PrimaryKey(autoGenerate = true) var id: Int = 0, var tgngu: Float) {
    override fun toString(): String {
        return "${tgngu}"
    }
}

@Entity(tableName = "lienKet",
    primaryKeys = ["userid", "timeSleepId", "timeAwakeId","tgnguId"],
    foreignKeys = [
        ForeignKey(entity = tgngu::class, parentColumns = ["id"], childColumns = ["tgnguId"]),
    ForeignKey(entity = timeSleep::class, parentColumns = ["id"], childColumns = ["timeSleepId"]),
    ForeignKey(entity = userData::class, parentColumns = ["userid"], childColumns = ["userid"], onDelete = CASCADE, onUpdate = CASCADE),
    ForeignKey(entity = timeAwake::class, parentColumns = ["id"], childColumns = ["timeAwakeId"]),
])
data class lienKet(
    var userid: Int = 0,
    var timeSleepId: Int = 0,
    var timeAwakeId: Int = 0,
    var tgnguId: Int = 0
)

@Entity(tableName = "userData")
data class userData(
    @PrimaryKey(autoGenerate = true)
    val userid: Int = 0,
    val userName: String,
    val email: String,
    val password: String,
    val role: String = "user",
    var login: Boolean = false
)

@Entity(tableName = "muctieuData", foreignKeys = arrayOf(ForeignKey(entity = userData::class, parentColumns = ["userid"], childColumns = ["userId"])))
data class muctieuData(
    @PrimaryKey(autoGenerate = true)
    var muctieuid: Int = 0,
    var userId: Int = 0,
    var mucTieuNgay: String,
    var mucTieuTuan: String,
    var mucTieuThang: String
)