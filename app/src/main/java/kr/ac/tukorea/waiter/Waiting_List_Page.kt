package kr.ac.tukorea.waiter

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_waiting_list_page.*
import kr.ac.tukorea.waiter.databinding.WaitingListPageLayoutBinding
import java.text.FieldPosition


class Waiting_List_Page : AppCompatActivity() {

//    private var Wbinding: DialogmenuBinding? = null
//    private val binding get() = Wbinding!!

    var db: FirebaseFirestore = Firebase.firestore

    lateinit var newbtn : Button

    data class UserInfo(
        val index: String? = null,
        val name: String? = null,
        val phone: String? = null,
        val customeNum: Int = 0
    )



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var waitingListInfo = arrayListOf<WaitingListInfo_useWaitingPage>()

        db = FirebaseFirestore.getInstance()

        val reserveInfo = db.collection("rest_Info").document("abc")
            .collection("reservation")



        reserveInfo.get().addOnSuccessListener { result ->

            if (result != null) {
                for (data in result) {
                    waitingListInfo.addAll(
                        listOf(
                            WaitingListInfo_useWaitingPage(
                                data.get("name").toString(),
                                data.get("phone").toString(),
                                data.get("customerNum").toString().toInt(),
                                data.get("index").toString().toInt()
                            )
                        )
                    )

                }
            } else {
                Log.d("TAG", "fail")
            }

            Log.d("Testdata","${waitingListInfo}")

            val Adapter = WaitingListAdapter(this, waitingListInfo)
            listView.adapter = Adapter



            listView.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
                val selection = parent.getItemAtPosition(position) as WaitingListInfo_useWaitingPage
                Toast.makeText(this, selection.name,Toast.LENGTH_SHORT).show()
                Log.d("Testdata","${position}")

                val indexpo = (position+1).toString()

                AlertDialog.Builder(this)  // 다이어로그 출력
                    .setView(R.layout.dialogmenu)
                    .show()
                    .also{ alertDialog ->
                        if(alertDialog == null){
                            return@also
                        }
//                        Wbinding = DialogmenuBinding.inflate(layoutInflater)
//                        setContentView(binding.root)  //바인딩 연결 시간 남으면
                        val button1 = alertDialog.findViewById<Button>(R.id.diabtn1)
                        val button2 = alertDialog.findViewById<Button>(R.id.diabtn2)
                        val infotext = alertDialog.findViewById<TextView>(R.id.infoText)

                        infotext.setText("${(position+1)}번째 리스트를 삭제")

                        button1?.setOnClickListener {
                            alertDialog.dismiss()
                            Log.d("Testdata","확인")

                            val remove = db.collection("rest_Info").document("abc")   //DB삭제
                                .collection("reservation").document("${indexpo}")
                            remove.delete().addOnSuccessListener {
                                Log.d("Testdata","succes")
                            }
                        }

                        button2?.setOnClickListener {
                            alertDialog.dismiss()
                            Log.d("Testdata","취소")
                        }

                    }


            }

        }

    }

    override fun onStart() {
        super.onStart()

        setContentView(R.layout.activity_waiting_list_page)
        var reintent = intent



    }
}