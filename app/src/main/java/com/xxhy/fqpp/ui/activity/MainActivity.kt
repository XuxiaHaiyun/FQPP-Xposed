package com.xxhy.fqpp.ui.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xxhy.fqpp.App
import com.xxhy.fqpp.R
import com.xxhy.fqpp.xposed.global.XposedGlobals
import io.github.libxposed.service.XposedService

class MainActivity : AppCompatActivity(), App.ServiceStateListener {

  private lateinit var statusCard: MaterialCardView
  private lateinit var statusBadge: ImageView
  private lateinit var statusTitle: TextView
  private lateinit var statusSub: TextView
  private lateinit var requestScopeButton: MaterialButton

  private val targetPackage = "com.dragon.read"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setSupportActionBar(findViewById(R.id.toolbar))

    statusCard = findViewById(R.id.statusCard)
    statusBadge = findViewById(R.id.statusBadge)
    statusTitle = findViewById(R.id.statusTitle)
    statusSub = findViewById(R.id.statusSub)
    requestScopeButton = findViewById(R.id.requestScopeButton)
    requestScopeButton.setOnClickListener { requestScope() }

    findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView).startAnimation(
      AnimationUtils.loadAnimation(this, R.anim.fade_in))

    applyStatus(App.service)

    // 预热创建配置 SP 文件，确保 hook 进程 getRemotePreferences 可用
    com.xxhy.fqpp.xposed.config.ConfigManager.modulePrefs(this).all
  }

  override fun onStart() {
    super.onStart()
    App.addServiceStateListener(this, true)
  }

  override fun onStop() {
    App.removeServiceStateListener(this)
    super.onStop()
  }

  override fun onServiceStateChanged(service: XposedService?) {
    runOnUiThread { applyStatus(service) }
  }

  private fun applyStatus(service: XposedService?) {
    val active = service != null || XposedGlobals.tryGetXposedEntryInstance() != null
    val card = MaterialColors.getColor(this,
      if (active) com.google.android.material.R.attr.colorPrimaryContainer
      else com.google.android.material.R.attr.colorErrorContainer, 0)
    val onCard = MaterialColors.getColor(this,
      if (active) com.google.android.material.R.attr.colorOnPrimaryContainer
      else com.google.android.material.R.attr.colorOnErrorContainer, 0)
    val badge = MaterialColors.getColor(this,
      if (active) androidx.appcompat.R.attr.colorPrimary
      else androidx.appcompat.R.attr.colorError, 0)
    val onBadge = MaterialColors.getColor(this,
      if (active) com.google.android.material.R.attr.colorOnPrimary
      else com.google.android.material.R.attr.colorOnError, 0)

    statusCard.setCardBackgroundColor(card)
    statusTitle.setTextColor(onCard)
    statusSub.setTextColor(onCard)
    statusBadge.backgroundTintList = ColorStateList.valueOf(badge)
    statusBadge.setImageResource(if (active) R.drawable.ic_check_circle else R.drawable.ic_error)
    statusBadge.imageTintList = ColorStateList.valueOf(onBadge)

    if (active) {
      statusTitle.setText(R.string.status_active)
      if (service != null) {
        val scope = runCatching { service.getScope() }.getOrNull() ?: emptyList()
        val scopeText = scope.joinToString("、").ifEmpty { "（空）" }
        val entry = XposedGlobals.tryGetXposedEntryInstance()
        statusSub.text = "框架：${service.getFrameworkName()} ${service.getFrameworkVersion()}（API ${service.getApiVersion()}）\n" +
          "作用域：$scopeText" +
          (if (entry != null) "\n番茄小说进程已注入模块" else "")
        requestScopeButton.visibility = if (scope.contains(targetPackage)) View.GONE else View.VISIBLE
      } else {
        statusSub.text = "模块已注入番茄小说进程，但模块 App 未连接框架服务。\n" +
          "若此页面一直停留在此状态，请在 LSPosed 中确认模块已启用。"
        requestScopeButton.visibility = View.GONE
      }
    } else {
      statusTitle.setText(R.string.status_inactive)
      statusSub.text = "未检测到框架服务或模块注入。\n" +
        "请在 LSPosed 中启用本模块，再点击下方按钮一键将番茄小说添加进作用域，然后重启番茄小说。"
      requestScopeButton.visibility = View.VISIBLE
    }
  }

  /** 一键请求把番茄小说加入模块作用域（libxposed service API）。 */
  private fun requestScope() {
    val service = App.service
    if (service == null) {
      Toast.makeText(this, "未连接框架服务，请先在 LSPosed 中启用模块", Toast.LENGTH_SHORT).show()
      return
    }
    service.requestScope(listOf(targetPackage), object : XposedService.OnScopeEventListener {
      override fun onScopeRequestApproved(approved: List<String>) {
        runOnUiThread {
          Toast.makeText(this@MainActivity, "已加入作用域：$approved，请重启番茄小说", Toast.LENGTH_LONG).show()
          applyStatus(service)
        }
      }

      override fun onScopeRequestFailed(message: String) {
        runOnUiThread {
          Toast.makeText(this@MainActivity, "请求失败：$message", Toast.LENGTH_LONG).show()
        }
      }
    })
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_config -> {
        startActivity(Intent(this, ConfigActivity::class.java))
        true
      }
      R.id.action_author -> {
        showAuthorDialog()
        true
      }
      R.id.action_disclaimer -> {
        showDisclaimerDialog()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun showAuthorDialog() {
    MaterialAlertDialogBuilder(this)
      .setTitle("关于作者")
      .setMessage(HtmlCompat.fromHtml("FQ++ 项目<br>By XuxiaHaiyun<br>仅供技术研究使用", HtmlCompat.FROM_HTML_MODE_LEGACY))
      .setPositiveButton("确定", null)
      .show()
  }

  private fun showDisclaimerDialog() {
    val disclaimer = "1. 本模块仅供 <b>技术研究、个人学习</b> 使用，严禁用于商业用途。<br><br>" +
      "2. 通过本模块获取的内容版权归原作者或平台所有，用户应仅为个人阅读使用，不得扩散或用于其他目的。<br><br>" +
      "3. 使用本模块可能违反相关平台用户协议，由此产生的账号封禁、法律风险由使用者自行承担。<br><br>" +
      "4. 项目开发者不承担任何因使用本模块导致的直接或间接损失。<br><br>" +
      "尊重知识产权，请支持正版。"
    MaterialAlertDialogBuilder(this)
      .setTitle("免责声明")
      .setMessage(HtmlCompat.fromHtml(disclaimer, HtmlCompat.FROM_HTML_MODE_LEGACY))
      .setPositiveButton("确定", null)
      .show()
  }
}
