package com.judahben149.emvsync.presentation.transactionResult

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.judahben149.emvsync.R
import com.judahben149.emvsync.databinding.FragmentTransactionResultBinding
import com.judahben149.emvsync.domain.model.card.TransactionData
import com.judahben149.emvsync.domain.model.card.TransactionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TransactionResultFragment : Fragment() {

    private var _binding: FragmentTransactionResultBinding? = null
    private val binding get() = _binding!!

    private val args: TransactionResultFragmentArgs by navArgs()
    private val viewModel: TransactionResultViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionData = args.transactionData
        viewModel.initializeTransactionData(transactionData)

        observeState()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.apply {
            btnCancel.setOnClickListener {
                navController.popBackStack(R.id.cardBalanceFragment, true)
            }
        }
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach { state ->
            state.transactionData?.let { populateViews(it) }

            if (state.isReadyToPrint) {
                binding.btnPrint.visibility = View.VISIBLE
            }

            if (state.isPrintingComplete) {
                //Give feedback and go to homeScreen
                navController.popBackStack(R.id.cardBalanceFragment, true)
            }
        }.launchIn(lifecycleScope)
    }

    private fun populateViews(transactionData: TransactionData) {

        if (transactionData.isTransactionSuccessful) {
            binding.run {
                animResult.setAnimation(R.raw.anim_success)
                tvCardNumber.text = transactionData.cardNo
                tvCardHolderName.text = transactionData.cardHolderName
                tvCardType.text = transactionData.appLabel
                tvCardExpiryDate.text = transactionData.expiryDate
                tvRefNumber.text = transactionData.stan

                when (transactionData.transType) {
                    TransactionType.BALANCE -> {
                        tvTransactionResultStatus.text = "Balance Enquiry Successful"
                        tvAmountText.text = "Account Balance: "
                        tvAmount.text = transactionData.accountBalance

                        viewModel.togglePrintable()
                    }
                    TransactionType.PURCHASE -> {
                        tvTransactionResultStatus.text = "Payment Successful"
                        tvAmount.text = transactionData.amount.toString()

                        viewModel.togglePrintable()
                    }
                    else -> {
                        tvTransactionResultStatus.text = "Operation  Successful"
                    }
                }
            }
        } else {
            binding.run {
                tvCardNumber.text = transactionData.cardNo
                tvCardHolderName.text = transactionData.cardHolderName
                tvCardType.text = transactionData.cardType
                tvCardExpiryDate.text = transactionData.expiryDate
                tvRefNumber.text = transactionData.stan

                when (transactionData.transType) {
                    TransactionType.BALANCE -> {
                        tvTransactionResultStatus.text = "Balance Enquiry Failed"
                        tvAmountText.visibility = View.GONE
                        tvAmount.visibility = View.GONE
                    }
                    TransactionType.PURCHASE -> {
                        tvTransactionResultStatus.text = "Payment Failed"
                        tvAmount.text = transactionData.amount.toString()

                        viewModel.togglePrintable()
                    }
                    else -> {
                        tvTransactionResultStatus.text = "Operation  Failed"
                        tableResult.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}