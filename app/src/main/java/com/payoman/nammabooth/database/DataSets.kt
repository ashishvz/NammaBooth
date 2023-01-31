package com.payoman.nammabooth.database

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId
import java.util.*

open class User(
    @PrimaryKey
    var id: ObjectId = ObjectId(),
    var userName: String? = null,
    var partNumber: Int? = null,
    var mobileNumber: Long? = null,
    var latitude: String? = null,
    var longitude: String? = null
) : RealmObject()

open class Candidate(
    @PrimaryKey
    var id: Int = 0,
    var candidatePhoneNumber: String? = null,
    var candidateName: String? = null,
    var partyId: Int? = null,
    var assemblyConstituencyNumber: String? = null,
    var constituencyName: String? = null,
    var candidateImageUrl: String? = null,
    var education: String? = null,
    var designation: String? = null,
    var candidateWebsite: String? = null,
    var partySymbolUrl: String? = null,
    var candidateManifestoUrl: String? = null,
    var bannerUrls: RealmList<String>? = null,
    var newsLetterUrls: RealmList<String>? = null,
    var urls: Urls? = null,
    var createdAt: Date? = null
) : RealmObject()

open class Urls(
    var facebook: String? = null,
    var twitter: String? = null,
    var youtube: String? = null,
    var Instagram: String? = null
): RealmObject()

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

open class UpdatePhoneNumber(
    var voterId: String? = null,
    var phoneNumber: String? = null,
    var latitude: String? = null,
    var longitude: String? = null
): RealmObject()