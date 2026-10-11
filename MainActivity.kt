package com.realsensi.app

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.security.MessageDigest

class MainActivity : Activity() {

    private val roxo = Color.rgb(166, 107, 255)
    private val fundo = Color.rgb(12, 12, 17)
    private val cartao = Color.rgb(27, 27, 37)
    private lateinit var campoKey: EditText
    private lateinit var lembrar: CheckBox
    private lateinit var olho: ImageButton
    private var mostrarKey = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("realsensi", Context.MODE_PRIVATE)
        if (prefs.getBoolean("logado", false)) {
            abrirPainel()
            return
        }
        mostrarLogin()
    }

    private fun fundoForma(cor: Int, raio: Float): GradientDrawable {
        return GradientDrawable().apply {
            setColor(cor)
            cornerRadius = raio
        }
    }

    private fun texto(valor: String, tamanho: Float, cor: Int): TextView {
        return TextView(this).apply {
            text = valor
            textSize = tamanho
            setTextColor(cor)
        }
    }

    private fun mostrarLogin() {
        val raiz = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 36, 28, 24)
            setBackgroundColor(fundo)
        }

        val titulo = texto("REAL SENSI", 30f, roxo).apply {
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        raiz.addView(titulo)

        raiz.addView(texto("Free Fire Tools", 16f, Color.LTGRAY).apply {
            gravity = Gravity.CENTER
        })

        val cartaoLogin = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 24, 22, 24)
            background = fundoForma(cartao, 28f)
        }

        val cabecalho = texto("Entrar com sua Key", 22f, Color.WHITE).apply {
            typeface = Typeface.DEFAULT_BOLD
        }
        cartaoLogin.addView(cabecalho)

        campoKey = EditText(this).apply {
            hint = "Digite sua Key"
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            textSize = 17f
            singleLine = true
            inputType = 129
            background = fundoForma(Color.rgb(40, 40, 52), 16f)
            setPadding(16, 12, 16, 12)
        }
        cartaoLogin.addView(campoKey, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = 22 })

        val botoes = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        olho = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_view)
            setColorFilter(roxo)
            background = fundoForma(cartao, 14f)
            contentDescription = "Mostrar ou ocultar Key"
            setOnClickListener {
                mostrarKey = !mostrarKey
                campoKey.inputType = if (mostrarKey) 1 else 129
                campoKey.setSelection(campoKey.text.length)
            }
        }
        botoes.addView(olho, LinearLayout.LayoutParams(54, 50))

        val colar = Button(this).apply {
            text = "Colar"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.TRANSPARENT)
            setOnClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                if (clipboard.hasPrimaryClip()) {
                    campoKey.setText(clipboard.primaryClip?.getItemAt(0)?.coerceToText(this@MainActivity))
                }
            }
        }
        botoes.addView(colar)
        cartaoLogin.addView(botoes)

        lembrar = CheckBox(this).apply {
            text = "Lembrar Key"
            setTextColor(Color.WHITE)
            buttonTintList = android.content.res.ColorStateList.valueOf(roxo)
            isChecked = getSharedPreferences("realsensi", MODE_PRIVATE)
                .getBoolean("lembrar", false)
        }
        cartaoLogin.addView(lembrar)

        val entrar = Button(this).apply {
            text = "Entrar"
            textSize = 17f
            setTextColor(Color.WHITE)
            background = fundoForma(roxo, 18f)
            setOnClickListener {
                val digitada = campoKey.text.toString().trim()
                if (MessageDigest.isEqual(
                    sha256(digitada.lowercase()),
                    sha256("realsensi")
                )) {
                    getSharedPreferences("realsensi", MODE_PRIVATE).edit()
                        .putBoolean("lembrar", lembrar.isChecked)
                        .putBoolean("logado", lembrar.isChecked)
                        .apply()
                    abrirPainel()
                } else {
                    Toast.makeText(this@MainActivity, "Key inválida", Toast.LENGTH_SHORT).show()
                }
            }
        }
        cartaoLogin.addView(entrar, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 56
        ).apply { topMargin = 18 })

        raiz.addView(cartaoLogin, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = 34 })

        raiz.addView(texto("O que o painel libera", 20f, Color.WHITE).apply {
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }, LinearLayout.LayoutParams(-1, -2).apply { topMargin = 28 })

        listOf("Sensi", "RealShot", "Ajustes", "Treino").forEach {
            val item = texto(it, 17f, roxo).apply {
                gravity = Gravity.CENTER
                background = fundoForma(cartao, 14f)
                setPadding(12, 16, 12, 16)
            }
            raiz.addView(item, LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = 8
            })
        }

        raiz.addView(texto("Real Sensi • versão 1.0", 12f, Color.GRAY).apply {
            gravity = Gravity.CENTER
        }, LinearLayout.LayoutParams(-1, -2).apply { topMargin = 24 })

        setContentView(raiz)
    }

    private fun sha256(valor: String): ByteArray {
        return MessageDigest.getInstance("SHA-256")
            .digest(valor.toByteArray(Charsets.UTF_8))
    }

    private fun abrirPainel() {
        val raiz = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 28, 28, 28)
            setBackgroundColor(fundo)
        }

        raiz.addView(texto("REAL SENSI", 30f, roxo).apply {
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        })

        raiz.addView(texto("Free Fire Tools", 16f, Color.LTGRAY).apply {
            gravity = Gravity.CENTER
        })

        val ativar = Button(this).apply {
            text = "Ativar painel flutuante"
            setTextColor(Color.WHITE)
            background = fundoForma(roxo, 18f)
            setOnClickListener {
                if (!android.provider.Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        android.net.Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                } else {
                    try {
                        val intent = android.content.Intent(
                            this@MainActivity, RealSensiService::class.java
                        )
                        if (android.os.Build.VERSION.SDK_INT >= 26) {
                            startForegroundService(intent)
                        } else {
                            startService(intent)
                        }
                        Toast.makeText(this@MainActivity, "Painel solicitado", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Confira o serviço do painel", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        raiz.addView(ativar, LinearLayout.LayoutParams(-1, 58).apply {
            topMargin = 30
        })

        val sair = Button(this).apply {
            text = "Sair da Key"
            setOnClickListener {
                getSharedPreferences("realsensi", MODE_PRIVATE).edit()
                    .putBoolean("logado", false).apply()
                mostrarLogin()
            }
        }
        raiz.addView(sair)
        setContentView(raiz)
    }
}
