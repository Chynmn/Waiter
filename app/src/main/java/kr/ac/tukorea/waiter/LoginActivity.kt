package kr.ac.tukorea.waiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.ac.tukorea.waiter.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener {
            val userEmail = binding.userid.text.toString()
            val password = binding.password.text.toString()

            if(binding.userid.text.toString().equals("")
                ||binding.password.text.toString().equals("")
            ){
                Toast.makeText(this, "로그인에 필요한 정보를 모두 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else {
                doLogin(userEmail, password)
            }
        }
//clickable 써보자


        binding.signinButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun doLogin(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { // it: Task<AuthResult!>
                if (it.isSuccessful) {
                    startActivity(
                        Intent(this, MapPage::class.java)
                    )
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "로그인 실패!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}