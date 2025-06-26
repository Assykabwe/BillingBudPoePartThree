package com.example.billingbudpoe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.billingbudpoe.databinding.SignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding: SignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.register.setOnClickListener {
            val name = binding.fullName.text.toString().trim()
            val email = binding.emailA.text.toString().trim()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmP.text.toString()

            if (name.isEmpty()) {
                binding.fullName.error = "Full Name is required"
                binding.fullName.requestFocus()
            } else if (email.isEmpty()) {
                binding.emailA.error = "Email is required"
                binding.emailA.requestFocus()
            } else if (password.isEmpty()) {
                binding.password.error = "Password is required"
                binding.password.requestFocus()
            } else if (confirmPassword.isEmpty()) {
                binding.confirmP.error = "Please confirm password"
                binding.confirmP.requestFocus()
            } else if (password != confirmPassword) {
                binding.confirmP.error = "Passwords do not match"
                binding.confirmP.requestFocus()
            } else {
                registerUser(name, email, password)
            }
        }

        binding.haveAccountLabel.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

                    val userMap = mapOf(
                        "fullName" to name,
                        "email" to email
                    )

                    userRef.setValue(userMap).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
