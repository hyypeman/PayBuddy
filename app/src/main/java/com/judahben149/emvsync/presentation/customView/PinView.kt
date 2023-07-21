package com.judahben149.emvsync.presentation.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.judahben149.emvsync.databinding.PinViewBinding

class PinView @JvmOverloads constructor(
    private val ctx: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attrs, defStyleAttr) {

    private var _binding: PinViewBinding? = null
    private val binding get() = _binding!!


    init {
        _binding = PinViewBinding.inflate(LayoutInflater.from(context), this)
        setPinLength()
    }

    fun setPinLength(length: Int = 0) {
        when (length) {
            0 -> {
                binding.tvPin1.visibility = View.INVISIBLE
                binding.tvPin2.visibility = View.INVISIBLE
                binding.tvPin3.visibility = View.INVISIBLE
                binding.tvPin4.visibility = View.INVISIBLE
            }

            1 -> {
                binding.tvPin1.visibility = View.VISIBLE
                binding.tvPin2.visibility = View.INVISIBLE
                binding.tvPin3.visibility = View.INVISIBLE
                binding.tvPin4.visibility = View.INVISIBLE
            }

            2 -> {
                binding.tvPin1.visibility = View.VISIBLE
                binding.tvPin2.visibility = View.VISIBLE
                binding.tvPin3.visibility = View.INVISIBLE
                binding.tvPin4.visibility = View.INVISIBLE
            }

            3 -> {
                binding.tvPin1.visibility = View.VISIBLE
                binding.tvPin2.visibility = View.VISIBLE
                binding.tvPin3.visibility = View.VISIBLE
                binding.tvPin4.visibility = View.INVISIBLE
            }

            4 -> {
                binding.tvPin1.visibility = View.VISIBLE
                binding.tvPin2.visibility = View.VISIBLE
                binding.tvPin3.visibility = View.VISIBLE
                binding.tvPin4.visibility = View.VISIBLE
            }

        }
    }
}