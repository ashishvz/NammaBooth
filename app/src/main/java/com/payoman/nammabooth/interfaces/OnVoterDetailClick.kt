package com.payoman.nammabooth.interfaces

import com.payoman.nammabooth.database.Voter

interface OnVoterDetailClick {
    fun onClick(voter: Voter)
}