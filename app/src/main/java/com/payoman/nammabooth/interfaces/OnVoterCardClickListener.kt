package com.payoman.nammabooth.interfaces

import com.payoman.nammabooth.database.Voter

interface OnVoterCardClickListener {
    fun OnClick(voter: Voter)
}