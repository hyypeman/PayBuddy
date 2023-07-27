package com.judahben149.emvsync.utils

import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.judahben149.emvsync.R
import com.judahben149.emvsync.presentation.shared.ConfirmationDialog
import com.judahben149.emvsync.presentation.shared.ProgressDialog
import com.judahben149.emvsync.utils.Constants.CONFIRMATION_DIALOG
import com.judahben149.emvsync.utils.Constants.PROGRESS_DIALOG

fun TextView.successfulColor() {
    this.setTextColor(resources.getColor(R.color.green))
}

fun TextView.failureColor() {
    this.setTextColor(resources.getColor(R.color.red))
    this.text = this.text.toString().plus(" - Failed!")
}

fun LottieAnimationView.setSuccess() {
    this.setMaxProgress(0.44f)
    this.setMinProgress(0.0f)
    this.pauseSuccess()
}

fun LottieAnimationView.setFailed() {
    this.setMaxProgress(0.95f)
    this.setMinProgress(0.49f)
    this.pauseFailed()
}

fun LottieAnimationView.setLoading() {
    this.setMaxProgress(0.282f)
    this.setMinProgress(0.0f)
}

fun LottieAnimationView.pauseSuccess() {
    this.setMaxProgress(0.44f)
    this.setMinProgress(0.439f)
}

fun LottieAnimationView.pauseFailed() {
    this.setMaxProgress(0.95f)
    this.setMinProgress(0.949f)
}

fun TextView.setDownloadedText(type: String) {
    this.text = "Downloaded $type Key"
}

fun TextView.setInjectedText(type: String) {
    this.text = "Injected $type Key"
}


fun showDialog(
    context: Context,
    title: String,
    body: String,
    positiveText: String = "OK",
    negativeText: String = "Cancel",
    positiveAction: () -> Unit,
    negativeAction: () -> Unit,
    isCancellableOnTouchOutside: Boolean = false
) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(body)
        .setPositiveButton(positiveText) { dialog, _ ->
            positiveAction()
            dialog.dismiss()
        }
        .setNegativeButton(negativeText) { dialog, _ ->
            negativeAction()
            dialog.dismiss()
        }
        .setCancelable(isCancellableOnTouchOutside)
        .show()
}

fun showProgressDialog(childFragmentManager: FragmentManager, dialogText: String) {
    val dialogFragment = ProgressDialog.newInstance(dialogText)
    dialogFragment.isCancelable = false
    dialogFragment.show(childFragmentManager, PROGRESS_DIALOG)
}

fun hideProgressDialog(childFragmentManager: FragmentManager) {
    val dialogFragment = childFragmentManager.findFragmentByTag(PROGRESS_DIALOG)
    if (dialogFragment != null) {
        (dialogFragment as ProgressDialog).dismiss()
    }
}

fun showConfirmationDialog(
    childFragmentManager: FragmentManager,
    title: String,
    body: String,
    positiveText: String = "OK",
    negativeText: String = "Cancel",
    actionPositive: () -> Unit,
    actionNegative: () -> Unit
) {
    val dialogFragment = ConfirmationDialog.newInstance(
        title,
        body,
        positiveText,
        negativeText,
        actionPositive,
        actionNegative
    )
    dialogFragment.isCancelable = false
    dialogFragment.show(childFragmentManager, CONFIRMATION_DIALOG)
}

fun hideConfirmationDialog(childFragmentManager: FragmentManager) {
    val dialogFragment = childFragmentManager.findFragmentByTag(CONFIRMATION_DIALOG)
    if (dialogFragment != null) {
        (dialogFragment as ConfirmationDialog).dismiss()
    }
}


fun Button.disableButton() {
    this.isEnabled = false
    this.setBackgroundColor(resources.getColor(R.color.grey_light))
}

fun Button.enableButton() {
    this.isEnabled = true
    this.setBackgroundColor(resources.getColor(R.color.deep_brown))
}

