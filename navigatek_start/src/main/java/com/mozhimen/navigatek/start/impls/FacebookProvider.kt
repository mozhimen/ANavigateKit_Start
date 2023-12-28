package com.mozhimen.navigatek.start.impls

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.mozhimen.basick.utilk.android.content.UtilKPackage
import com.mozhimen.basick.utilk.android.content.UtilKPackageManager
import com.mozhimen.basick.utilk.android.content.startContext
import com.mozhimen.navigatek.start.commons.IProvider


/**
 * @ClassName Facebook
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2023/12/29 1:14
 * @Version 1.0
 */
object FacebookProvider : IProvider {
    override val PACKAGE_NAME = "com.facebook.katana"

    /////////////////////////////////////////////////////////////

    @JvmStatic
    fun startContext(context: Context) {
        var intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)
        if (intent == null) {
            intent = Intent()
            intent.action = "android.intent.action.VIEW"
            intent.data = Uri.parse("http://facebook.com/")// 打开url
            context.startContext(intent)
        } else {
            /* intent.flags =
                 Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_CLEAR_TOP*/
            context.startContext(intent)
        }
    }

    /**
     * In Facebook version 11.0.0.11.23 (3002850) fb://profile/ and fb://page/ no longer work. I decompiled the Facebook app and found that you can use fb://facewebmodal/f?href=[YOUR_FACEBOOK_PAGE]. Here is the method I have been using in production:
     * @param context Context
     * @param strUrl String
     */
    @JvmStatic
    fun startContext(context: Context, facebookPageId: String, strUrl: String) {
        try {
            val isInstall = UtilKPackageManager.isInstalled(context, PACKAGE_NAME)
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    if (!isInstall)
                        strUrl
                    else
                        getFacebookDetailURL(strUrl)
                )
            )
            context.startContext(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            // 处理Facebook应用未安装的情况
            // 可以在这里打开网页版Facebook或提示用户安装Facebook应用
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/$facebookPageId"))
                intent.setPackage(PACKAGE_NAME) // 指定要使用Facebook应用打开链接
                context.startContext(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    /////////////////////////////////////////////////////////////

    /**
     * 直接链接
     */
    private fun getFacebookDetailURL(strUrl: String): String {
        try {
            if (UtilKPackage.getVersionCode() >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=$strUrl"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return strUrl
    }
}