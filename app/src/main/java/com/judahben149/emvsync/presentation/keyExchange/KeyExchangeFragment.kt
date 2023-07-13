package com.judahben149.emvsync.presentation.keyExchange

import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.judahben149.emvsync.R
import com.judahben149.emvsync.databinding.FragmentKeyExchangeBinding
import com.judahben149.emvsync.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KeyExchangeFragment : Fragment() {

    private var _binding: FragmentKeyExchangeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: KeyExchangeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKeyExchangeBinding.inflate(inflater, container, false)
        setViewLoadingState()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateUiState(state)
                }
            }
        }

        viewModel.doKeyExchange()
    }

    //Really hacky way to make Lottie progress bar animation work as I want, for now
    private fun updateUiState(state: KeyExchangeState) {
        state.toString().logThis("Tagg")

        binding.apply {
            if (state.isMasterKeyReceived && !state.isSessionKeyReceived && !state.isPinKeyReceived && !state.isParameterReceived && !state.isMasterKeyInjected && !state.isPinKeyInjected) {
                tvMasterKey.successfulColor()
                tvMasterKey.setDownloadedText("Master")
                animPgBarMasterKey.setSuccess()
            }
            if (state.isMasterKeyReceived && state.isSessionKeyReceived && !state.isPinKeyReceived && !state.isParameterReceived && !state.isMasterKeyInjected && !state.isPinKeyInjected) {
                tvSessionrKey.successfulColor()
                tvSessionrKey.setDownloadedText("Session")
                animPgBarSessionKey.setSuccess()
            }

            //isPinKeyReceived and isParameterReceived become true at the same time
            if (state.isMasterKeyReceived && state.isSessionKeyReceived && state.isPinKeyReceived && !state.isMasterKeyInjected && !state.isPinKeyInjected) {
                tvPinKey.successfulColor()
                tvPinKey.setDownloadedText("Pin")
                animPgBarPinKey.setSuccess()
            }
            if (state.isMasterKeyReceived && state.isSessionKeyReceived && state.isParameterReceived && !state.isMasterKeyInjected && !state.isPinKeyInjected) {
                tvParameterDownload.successfulColor()
                tvParameterDownload.text = "Downloaded Terminal Parameters"
                animPgBarParameterDownload.setSuccess()
            }
            if (state.isMasterKeyReceived && state.isSessionKeyReceived && state.isPinKeyReceived && state.isParameterReceived && state.isMasterKeyInjected && !state.isPinKeyInjected) {
                tvInjectingMasterKey.successfulColor()
                tvInjectingMasterKey.setInjectedText("Master")
                animPgBarInjectingMasterKey.setSuccess()
            }
            if (state.isMasterKeyReceived && state.isSessionKeyReceived && state.isPinKeyReceived && state.isParameterReceived && state.isMasterKeyInjected && state.isPinKeyInjected) {
                tvInjectingPinKey.successfulColor()
                tvInjectingPinKey.setInjectedText("Pin")
                animPgBarInjectingPinKey.setSuccess()
            }


            if (state.isFailure) {
                tvMasterKey.failureColor()
                tvSessionrKey.failureColor()
                tvPinKey.failureColor()
                tvParameterDownload.failureColor()
                tvInjectingMasterKey.failureColor()
                tvInjectingPinKey.failureColor()

                animPgBarMasterKey.setFailed()
                animPgBarSessionKey.setFailed()
                animPgBarPinKey.setFailed()
                animPgBarParameterDownload.setFailed()
                animPgBarInjectingMasterKey.setFailed()
                animPgBarInjectingPinKey.setFailed()
            }
        }
    }

    private fun setViewLoadingState() {

        binding.apply {
            animPgBarMasterKey.setLoading()
            animPgBarSessionKey.setLoading()
            animPgBarPinKey.setLoading()
            animPgBarParameterDownload.setLoading()
            animPgBarInjectingMasterKey.setLoading()
            animPgBarInjectingPinKey.setLoading()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}