package com.judahben149.emvsync.presentation.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.judahben149.emvsync.databinding.BalanceCardViewBinding

class BalanceCard @JvmOverloads constructor(
    private val ctx: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
): ConstraintLayout(ctx, attrs, defStyleAttr) {

    private var _binding: BalanceCardViewBinding? = null
    private val binding get() = _binding!!

    private var clickListener: (() -> Unit)? = null

    init {
        _binding = BalanceCardViewBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setDate(date: String) {
        binding.tvDate.text = date
    }

    fun setAmount(amount: String) {
        binding.tvBalance.text = amount
    }

    fun setOnClickListener(listener: () -> Unit) {
        clickListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {
        val handled = super.performClick()
        clickListener?.invoke()
        return handled
    }
}