package com.judahben149.emvsync.presentation.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.judahben149.emvsync.R
import com.judahben149.emvsync.databinding.FragmentPurchaseBinding
import com.judahben149.emvsync.utils.Constants.IS_PURCHASE_TRANSACTION
import com.judahben149.emvsync.utils.Constants.PURCHASE_AMOUNT
import com.judahben149.emvsync.utils.disableButton
import com.judahben149.emvsync.utils.enableButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PurchaseFragment : Fragment() {

    private var _binding: FragmentPurchaseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PurchaseViewModel by viewModels()
    val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonClickListeners()
        observeState()

        binding.btnContinue.setOnClickListener {
            val amount = viewModel.state.value.amount.toDouble()
            val isPurchaseTransaction = true
            val bundle = Bundle().apply {
                putDouble(PURCHASE_AMOUNT, amount)
                putBoolean(IS_PURCHASE_TRANSACTION, isPurchaseTransaction)
            }

            navController.navigate(R.id.action_purchaseFragment_to_cardBalanceFragment, bundle)
        }

        binding.btnCancel.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach {  state ->
            binding.tvAmountField.text = state.amount

            if(state.amount.isEmpty()) {
                binding.btnContinue.disableButton()
            } else {
                binding.btnContinue.enableButton()
            }
        }.launchIn(lifecycleScope)
    }


    private fun setButtonClickListeners() {
        with(binding) {
            tvOneKey.setOnClickListener {
                viewModel.addNumber("1")
            }
            tvTwoKey.setOnClickListener {
                viewModel.addNumber("2")
            }
            tvThreeKey.setOnClickListener {
                viewModel.addNumber("3")
            }
            tvFourKey.setOnClickListener {
                viewModel.addNumber("4")
            }
            tvFiveKey.setOnClickListener {
                viewModel.addNumber("5")
            }
            tvSixKey.setOnClickListener {
                viewModel.addNumber("6")
            }
            tvSevenKey.setOnClickListener {
                viewModel.addNumber("7")
            }
            tvEightKey.setOnClickListener {
                viewModel.addNumber("8")
            }
            tvNineKey.setOnClickListener {
                viewModel.addNumber("9")
            }
            tvZeroKey.setOnClickListener {
                viewModel.addNumber("0")
            }
            tvClearKey.setOnClickListener {
                viewModel.clear()
            }
            tvCancelKey.setOnClickListener {
                viewModel.cancel()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}