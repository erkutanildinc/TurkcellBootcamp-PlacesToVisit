package com.example.placestovisit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var loginBtn : Button
    lateinit var emailEditText : EditText
    lateinit var passwordEditText: EditText
    lateinit var signUp : TextView
    lateinit var forgotPasswordText : TextView
    private lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn = findViewById(R.id.loginBtn)
        emailEditText = findViewById(R.id.loginUserEmailEditText)
        passwordEditText = findViewById(R.id.loginUserPasswordEditText)
        signUp = findViewById(R.id.signUpTextView)
        forgotPasswordText = findViewById(R.id.forgotPssword)
        mAuth = FirebaseAuth.getInstance()

        signUp.setOnClickListener(signUpClick)
        loginBtn.setOnClickListener(loginBtnClick)
        forgotPasswordText.setOnClickListener(forgotPasswordClick)
    }

    var signUpClick = View.OnClickListener {
        var intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
    }

    var forgotPasswordClick = View.OnClickListener {
        if(!emailEditText.text.isEmpty()){
            val userEmail = emailEditText.text.toString()
            mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener {
                Toast.makeText(this,"Email has been sent",Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(this,"Please enter an email",Toast.LENGTH_LONG).show()
        }

    }

    var loginBtnClick = View.OnClickListener {

        var userEmail = emailEditText.text.toString()
        var userPassword = passwordEditText.text.toString()

        if(userEmail.isEmpty() || userPassword.isEmpty()){
            Toast.makeText(this,"Email or Password is empty",Toast.LENGTH_LONG).show()
        }
        else{
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(this,"Login is success",Toast.LENGTH_LONG).show()
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}