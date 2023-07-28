package com.judahben149.emvsync.presentation.shared

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.judahben149.emvsync.R
import com.judahben149.emvsync.databinding.ConfirmationDialogLayoutBinding

class ConfirmationDialog(
    private val title: String,
    private val body: String,
    private val positiveText: String,
    private val negativeText: String,
    private val actionPositive: () -> Unit,
    private val actionNegative: () -> Unit,
) : DialogFragment() {

    private var _binding: ConfirmationDialogLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ConfirmationDialogLayoutBinding.inflate(inflater, container, false)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog)

        binding.tvConfirmationTitle.text = title
        binding.tvConfirmationBody.text = body
        binding.btnPositive.text = positiveText
        binding.btnNegative.text = negativeText

        binding.btnPositive.setOnClickListener {
            actionPositive()
            dismiss()
        }
        binding.btnNegative.setOnClickListener {
            actionNegative()
            dismiss()
        }

        return binding.root
    }

    companion object {
        fun newInstance(
            title: String,
            body: String,
            positiveText: String,
            negativeText: String,
            actionPositive: () -> Unit,
            actionNegative: () -> Unit,
        ): ConfirmationDialog {
            return ConfirmationDialog(
                title,
                body,
                positiveText,
                negativeText,
                actionPositive,
                actionNegative
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}