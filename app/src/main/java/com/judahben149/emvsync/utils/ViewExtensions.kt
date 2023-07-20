package com.judahben149.emvsync.utils

import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judahben149.emvsync.R

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

