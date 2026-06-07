package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable

/**
 * 用户数据实体类（Parcelable版 - 手动实现）
 *
 * 实现Parcelable接口：
 *   - Android推荐的序列化方式（方式4：Parcelable传对象）
 *   - 性能比Serializable高10倍以上（直接内存操作，无反射）
 *   - 企业首选传参方式
 *
 * 使用方式：
 *   发送：intent.putExtra("user_p", userParcel)
 *   接收：intent.getParcelableExtra<UserParcel>("user_p")
 *
 * @param account 账号
 * @param pwd     密码
 * @param sex     性别
 * @param hobby   爱好
 * @param city    所在城市
 */
data class UserParcel(
    val account: String,
    val pwd: String,
    val sex: String,
    val hobby: String,
    val city: String
) : Parcelable {

    // 从Parcel中读取数据恢复对象
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    // 将对象数据写入Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(account)
        parcel.writeString(pwd)
        parcel.writeString(sex)
        parcel.writeString(hobby)
        parcel.writeString(city)
    }

    // 内容描述，一般返回0即可
    override fun describeContents(): Int {
        return 0
    }

    // CREATOR：Parcelable的工厂对象，系统通过它创建对象
    companion object CREATOR : Parcelable.Creator<UserParcel> {
        override fun createFromParcel(parcel: Parcel): UserParcel {
            return UserParcel(parcel)
        }

        override fun newArray(size: Int): Array<UserParcel?> {
            return arrayOfNulls(size)
        }
    }
}