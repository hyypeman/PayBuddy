package com.judahben149.emvsync.presentation.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.judahben149.emvsync.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()

        binding.btnUpdate.setOnClickListener {
            binding.apply {
                viewModel.updateParameters(
                    terminalNo = tvTerminal.text.toString(),
                    acquirerId = tvAcquiringId.text.toString(),
                )
            }
            navController.popBackStack()
        }
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach { state ->
            binding.apply {

                tvTerminal.setText(state.terminalNumber)
                tvAcquiringId.setText(state.acquiringInstitutionCode)
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}