package etc

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Context.openUrl(url: String): Boolean {
    val urlBuilder = StringBuilder()

    if (url.startsWith("www.") || !url.contains("http")) {
        urlBuilder.append("http://")
    }

    urlBuilder.append(url)
    val intentBuilder = CustomTabsIntent.Builder()
    intentBuilder.setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
    intentBuilder.setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
    val customTabsIntent = intentBuilder.build()

    return try {
        customTabsIntent.launchUrl(this, Uri.parse(urlBuilder.toString()))
        true
    } catch (e : Exception) {
        false
    }
}

fun Context.showKeyboard(view: View) {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.showSoftInput(view, 0)
}

fun Context.hideKeyboard(view: View) {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(view.windowToken, 0)
}