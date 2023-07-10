package com.judahben149.emvsync.presentation.emv

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.judahben149.emvsync.databinding.FragmentKeyExchangeBinding
import dagger.hilt.android.AndroidEntryPoint

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.doKeyExchange()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}