package com.example.iglite.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.iglite.databinding.ActivityRegisterBinding
import com.example.iglite.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        playAnimation()
        checkPassword()

        binding?.btnRegister?.setOnClickListener {
            val name = binding?.edRegisterName?.text.toString()
            val email = binding?.edRegisterEmail?.text.toString()
            val password = binding?.edRegisterPassword?.text.toString()

            if (name.isEmpty()) {
                binding?.edRegisterName?.error = "Silahkan masukan nama"
            }
            if (email.isEmpty()) {
                binding?.edRegisterEmail?.error = "Silahkan masukan email"
            }
            if (password.isEmpty()) {
                binding?.edRegisterPassword?.error = "Silahkan masukan password"
            } else {
                viewModel.instagramLiteSubmitRegister(name, email, password)
                checkRegisterResult()
            }
        }

        binding?.tvRegisterLogin?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkRegisterResult() {
        viewModel.registerResult.observe(this) {
            if (it.error == false) {
                val intent = Intent(
                    this, LoginActivity::class.java
                )
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Register gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPassword() {
        binding?.edRegisterPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding!!.btnRegister.isEnabled = (s.length >= 8)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.tvRegisterTitle, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val nameEdit =
            ObjectAnimator.ofFloat(binding?.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val emailEdit =
            ObjectAnimator.ofFloat(binding?.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding?.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val login =
            ObjectAnimator.ofFloat(binding?.tvRegisterLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding?.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(nameEdit, emailEdit, passwordEdit, register, login)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}