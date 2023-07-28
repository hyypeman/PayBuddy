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
import com.judahben149.emvsync.databinding.ProgressDialogLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProgressDialog(private val text: String) : DialogFragment() {

    private var _binding: ProgressDialogLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProgressDialogLayoutBinding.inflate(inflater, container, false)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog)

        binding.tvProgressText.text = text
        return binding.root
    }

    companion object {
        fun newInstance(text: String): ProgressDialog {
            return ProgressDialog(text)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}