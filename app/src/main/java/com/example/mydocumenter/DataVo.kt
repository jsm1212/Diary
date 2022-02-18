package com.example.mydocumenter

import android.os.Parcel
import android.os.Parcelable

class DataVo(var seq:Int, var path:String?, var address:String?, var content:String?, var reg:String?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(seq)
        parcel.writeString(path)
        parcel.writeString(address)
        parcel.writeString(content)
        parcel.writeString(reg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataVo> {
        override fun createFromParcel(parcel: Parcel): DataVo {
            return DataVo(parcel)
        }

        override fun newArray(size: Int): Array<DataVo?> {
            return arrayOfNulls(size)
        }
    }

}