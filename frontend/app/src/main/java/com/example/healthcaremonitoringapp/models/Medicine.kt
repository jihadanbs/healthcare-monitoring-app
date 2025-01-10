package com.example.healthcaremonitoringapp.models

import android.os.Parcel
import android.os.Parcelable

data class Medicine(
    val id: String,
    val medicine: String,
    val dosage: String,
    val frequency: String,
    val status: PurchaseStatus,
    val price: Int
) : Parcelable {
    // Konstruktor untuk membaca dari Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        PurchaseStatus.valueOf(parcel.readString() ?: PurchaseStatus.NOT_PURCHASED.name),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(medicine)
        parcel.writeString(dosage)
        parcel.writeString(frequency)
        parcel.writeString(status.name)
        parcel.writeInt(price)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Medicine> {
        override fun createFromParcel(parcel: Parcel): Medicine = Medicine(parcel)
        override fun newArray(size: Int): Array<Medicine?> = arrayOfNulls(size)
    }
}

//data class Medicine(
//    val id: String,
//    val medicine: String,
//    val dosage: String,
//    val frequency: String,
//    val status: PurchaseStatus,
//    val price: Int
//)

enum class PurchaseStatus {
    NOT_PURCHASED,
    IN_PROGRESS,
    PURCHASED
}