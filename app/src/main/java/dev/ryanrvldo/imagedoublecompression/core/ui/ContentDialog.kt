package dev.ryanrvldo.imagedoublecompression.core.ui

import android.content.Context
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.ryanrvldo.imagedoublecompression.R

class ContentDialog(context: Context) {
    private val loadingDialog = MaterialAlertDialogBuilder(context)
        .setView(View.inflate(context, R.layout.content_dialog, null))
        .setCancelable(false)
        .create()

    fun showDialog() = loadingDialog.show()

    fun closeDialog() = loadingDialog.dismiss()

}
