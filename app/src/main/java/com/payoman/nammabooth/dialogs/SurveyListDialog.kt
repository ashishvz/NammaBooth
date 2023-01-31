package com.payoman.nammabooth.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.DialogSurveyBinding

class SurveyListDialog: DialogFragment() {

    private lateinit var binding: DialogSurveyBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()), R.layout.dialog_survey, null, false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)


        return dialog
    }
}