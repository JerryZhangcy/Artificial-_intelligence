package com.android.artificialintelligence

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.ActivityCompat
import android.net.Uri.fromParts
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.content.Intent

class PermissionActivity : AppCompatActivity() {
    private val REQUEST_CODE_OPEN_SETTING_PAGE = 0
    private val REQUEST_CODE_REQUEST_PERMISSION = 1
    private lateinit var mPermissionDialog: AlertDialog

    private var PERMISSIONS_STORAGE: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.permission_main)

        val unGrantPermission = getUnGrantedPermission()
        if (unGrantPermission == null) {
            startMainActivity()
        } else {
            requestPermission(unGrantPermission)
        }
    }

    private fun startMainActivity() {
        var mainIntent = Intent()
        mainIntent.setClass(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun getUnGrantedPermission(): Array<String>? {
        var requestPermission: ArrayList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in PERMISSIONS_STORAGE) {
                var state = ContextCompat.checkSelfPermission(this, permission)
                if (state != PackageManager.PERMISSION_GRANTED) {
                    requestPermission.add(permission)
                }
            }
        }

        return if (requestPermission.isNotEmpty()) {
            val permissionArray = arrayOfNulls<String>(requestPermission.size)
            requestPermission.toArray(permissionArray)
        } else {
            null
        }
    }

    private fun requestPermission(permissions: Array<String>?) {
        if (permissions != null && permissions.size != 0) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_REQUEST_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_REQUEST_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        // 判断用户是否点击了不再提醒。(检测该权限是否还可以申请)
                        val doNotShow = shouldShowRequestPermissionRationale(permissions[0])
                        if (!doNotShow) {
                            // 用户还是想用我的 APP 的
                            // 提示用户去应用设置界面手动开启权限
                            showDialogTipUserGoToAppSettting()
                            return
                        } else {
                            finish()
                            return
                        }
                    }
                }
                startMainActivity()
            }
        }
    }

    private fun showDialogTipUserGoToAppSettting() {
        mPermissionDialog = AlertDialog.Builder(this)
            .setTitle(R.string.permission_dialog_title)
            .setMessage(R.string.permission_dialog_msg)
            .setPositiveButton(
                R.string.permission_dialog_pos,
                DialogInterface.OnClickListener { dialog, which ->
                    goToAppSetting()
                    dialog.dismiss()
                })
            .setNegativeButton(
                R.string.permission_dialog_neg,
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    finish()
                }).setCancelable(false).show()
    }

    private fun goToAppSetting() {
        val intent = Intent()
        intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, REQUEST_CODE_OPEN_SETTING_PAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OPEN_SETTING_PAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val unGrantPermission = getUnGrantedPermission()
                if (unGrantPermission != null && unGrantPermission.isNotEmpty()) {
                    requestPermission(unGrantPermission)
                } else {
                    if (mPermissionDialog != null && mPermissionDialog.isShowing) {
                        mPermissionDialog.dismiss()
                    }
                    startMainActivity()
                }
            }
        }
    }
}