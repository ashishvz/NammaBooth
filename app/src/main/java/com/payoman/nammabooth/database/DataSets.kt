package com.payoman.nammabooth.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId
import java.util.UUID

open class User(
    @PrimaryKey
    var id: ObjectId = ObjectId(),

    @Required
    var userName: String? = null,

    var partNumber: Int? = null,

    var mobileNumber: Long? = null,

    @Required
    var latitude: String? = null,

    @Required
    var longitude: String? = null
) : RealmObject()

open class Voter(
    @PrimaryKey
    var id: ObjectId = ObjectId(),
    var partNo: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var acno: String? = null,
    var sectionNo: String? = null,
    var sno: String? = null,
    var houseNoEn: String? = null,
    var houseNo: String? = null,
    var voterNameEn: String? = null,
    var voterNameKan: String? = null,
    var sex: String? = null,
    var relationNameEn: String? = null,
    var relationNameKan: String? = null,
    var relationType: String? = null,
    var age: String? = null,
    var voterId: String? = null,
    var statusType: String? = null,
    var poolingStation: String? = null,
    var sectionNameEn: String? = null,
    var sectionNameKan: String? = null,
    var districtNo: String? = null,
    var pincode: String? = null,
    var houseHeadMobileNo: String? = null,
    var houseHeadNameAndMembers: String? = null,
    var houseHeadRelationship: String? = null,
    var houseHeadGender: String? = null,
    var religion: String? = null,
    var caste: String? = null,
    var motherTongue: String? = null,
    var electionCommissionIdentityCardNo: String? = null,
    var mobileNo: String? = null
) : RealmObject()