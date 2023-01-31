package com.payoman.nammabooth.repository

import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.apiOperations.UpdatePhoneNumberApiOperation
import com.payoman.nammabooth.database.UpdatePhoneNumber
import com.payoman.nammabooth.database.UpdatePhoneOperation
import com.payoman.nammabooth.models.DefaultResponse
import retrofit2.Response
import javax.inject.Inject

class UpdatePhoneNumberRepository @Inject constructor(
    private val updatePhoneOperation: UpdatePhoneOperation,
    private val updatePhoneNumberApiOperation: UpdatePhoneNumberApiOperation
): ViewModel() {
    suspend fun insertUpdatePhoneNumber(updatePhoneNumber: UpdatePhoneNumber) {
        updatePhoneOperation.insertUpdatePhoneNumber(updatePhoneNumber)
    }

    suspend fun getAllUpdatePhone(): MutableList<UpdatePhoneNumber> {
        return updatePhoneOperation.getAllUpdatePhoneNumber()
    }

    suspend fun deleteAllUpdatePhone() {
        updatePhoneOperation.deleteAllUpdatePhone()
    }

    suspend fun postUpdatePhoneNumber(updatePhoneNumber: MutableList<UpdatePhoneNumber>): Response<DefaultResponse> {
        return updatePhoneNumberApiOperation.updatePhoneNumber(updatePhoneNumber)
    }
}