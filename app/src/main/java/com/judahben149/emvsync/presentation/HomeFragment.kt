package com.judahben149.emvsync.presentation

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.judahben149.emvsync.R
import com.judahben149.emvsync.databinding.FragmentHomeBinding
import com.judahben149.emvsync.utils.Constants.CARD_HOLDER_NAME
import com.judahben149.emvsync.utils.Constants.LAST_BALANCE
import com.judahben149.emvsync.utils.fetchString
import com.judahben149.emvsync.utils.saveString
import com.judahben149.emvsync.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy {
        findNavController()
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnPurchase.setOnClickListener {
                navController.navigate(R.id.action_homeFragment_to_purchaseFragment)
            }

            btnKeyExchange.setOnClickListener {
                navController.navigate(R.id.action_homeFragment_to_emvFragment)
            }

            btnBalance.setOnClickListener {
                navController.navigate(R.id.action_homeFragment_to_cardBalanceFragment)
            }

            btnSettings.setOnClickListener {
                navController.navigate(R.id.action_homeFragment_to_settingsFragment)
            }


            cardBalance.setOnClickListener {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        val lastAmount = sharedPreferences.fetchString(LAST_BALANCE)
        val cardHolderName = sharedPreferences.fetchString(CARD_HOLDER_NAME)

        lastAmount?.let {
            binding.cardBalance.setAmount(it)
            binding.cardBalance.setDate(cardHolderName.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}