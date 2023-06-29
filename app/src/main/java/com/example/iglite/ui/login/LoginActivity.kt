package com.example.iglite.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.iglite.databinding.ActivityLoginBinding
import com.example.iglite.ui.main.MainActivity
import com.example.iglite.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        playAnimation()

        viewModel.getUserToken()
        checkUserToken()
        checkPassword()

        binding?.btnLogin?.setOnClickListener {
            val email = binding?.edLoginEmail?.text.toString()
            val password = binding?.edLoginPassword?.text.toString()
            if (email.isEmpty()) {
                binding?.edLoginEmail?.error = "Silahkan masukan email"
            }
            if (password.isEmpty()) {
                binding?.edLoginPassword?.error = "Silahkan masukan password"
            } else {
                viewModel.instagramLiteSubmitLogin(email, password)
                checkLoginResult()
            }
        }

        binding?.tvLoginRegister?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLoginResult() {
        viewModel.loginResult.observe(this) {
            if (it.loginResult?.token?.isNotEmpty() == true) {
                viewModel.saveUserToken(it.loginResult.token)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserToken() {
        viewModel.userToken.observe(this) {
            Log.d("LoginActivity", "checkUserToken: $it")
            if (it.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finishAffinity()
            }
        }
    }

    private fun checkPassword() {
        binding?.edLoginPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding!!.btnLogin.isEnabled = (s.length >= 8)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.tvLoginTitle, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val emailEdit =
            ObjectAnimator.ofFloat(binding?.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding?.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).setDuration(500)
        val signUp =
            ObjectAnimator.ofFloat(binding?.tvLoginRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(emailEdit, passwordEdit, login, signUp)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}