package com.eq.earthquakeplayer3

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.eq.earthquakeplayer3.data.PermissionConstants
import com.eq.earthquakeplayer3.utils.DialogUtils
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.toObservable

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"

        private val permissionList = arrayOf(
            PermissionConstants.PERMISSIONS_READ_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission(permissionList)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Observables.zip(permissions.toObservable(), grantResults.toObservable())
            .filter {
                it.second != PackageManager.PERMISSION_GRANTED
            }
            .count()
            .subscribe { permissionCount: Long?, _: Throwable? ->
                if (permissionCount == 0L) { //zero means all permissions granted
                    // init view (navigation에서 자동 처리됨)
                } else {
                    val title = getString(R.string.dialog_notice)
                    val body = getString(R.string.dialog_permission_body)
                    val builder: AlertDialog.Builder = DialogUtils.createCommonDialog(this@MainActivity, title, body)
                    builder.setPositiveButton(getString(R.string.dialog_request)) { _, _ -> requestPermission(permissionList) }
                    builder.setNegativeButton(getString(R.string.dialog_cancel)) { _, _ -> finish() }
                    builder.show()
                }
            }
    }

    private fun requestPermission(permissionArray: Array<String>) {
        permissionArray.toObservable()
            .filter {
                ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            .toList()
            .map {
                permissionArray.sortedArray()
            }
            .subscribe { permissions: Array<String>?, _: Throwable? ->
                permissions?.let {
                    ActivityCompat.requestPermissions(this, it, 0)
                }
            }
    }
}