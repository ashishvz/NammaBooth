package com.payoman.nammabooth.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.FragmentSplashscreenBinding
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.viewmodels.OtpViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashscreenBinding
    private var timer: CountDownTimer? = null
    private val otpViewModel: OtpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_splashscreen, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                timer!!.cancel()
                runBlocking {
                    if (AppUtils.DataStorePreference.getIsLoggedInFromDataStorePreference(
                            otpViewModel.provideDataStoreInstance()
                        ).first().isLoggedIn
                    ) {
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
                    } else {
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOtpFragment())
                    }
                }
            }
        }
        timer!!.start()
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_CONTACTS), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                timer!!.start()
            } else {
                Toast.makeText(requireActivity(), "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        timer!!.start()
    }
}