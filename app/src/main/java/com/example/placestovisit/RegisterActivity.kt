package com.example.placestovisit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    lateinit var registerBtn : Button
    lateinit var emailEditText : EditText
    lateinit var passwordEditText: EditText
    lateinit var passwordAgainEditText: EditText
    lateinit var signIn : TextView
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerBtn = findViewById(R.id.registerBtn)
        emailEditText = findViewById(R.id.userEmailEditText)
        passwordEditText = findViewById(R.id.userPasswordEditText)
        passwordAgainEditText = findViewById(R.id.userPasswordAgainEditText)
        signIn = findViewById(R.id.signInTextView)
        mAuth = FirebaseAuth.getInstance()

        registerBtn.setOnClickListener(registerBtnClick)
        signIn.setOnClickListener(signInClick)
    }


    var signInClick = View.OnClickListener {
        var intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    var registerBtnClick = View.OnClickListener {

        var userEmail = emailEditText.text.toString()
        var userPassword = passwordEditText.text.toString()
        var userPasswordAgain = passwordAgainEditText.text.toString()

        if(userPassword != userPasswordAgain || userEmail.isEmpty()){
            Toast.makeText(this,"Please check your inputs",Toast.LENGTH_LONG).show()
        }
        else{
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser
                        Toast.makeText(this,"Registiration Succesfull",Toast.LENGTH_LONG).show()
                        intent = Intent(applicationContext,LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed. Password must be at least 6 characters",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}