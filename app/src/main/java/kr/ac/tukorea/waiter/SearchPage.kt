package kr.ac.tukorea.waiter

import ResultSearchKeyword
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import kr.ac.tukorea.waiter.MapPage.Companion.listAdapter
import kr.ac.tukorea.waiter.databinding.ActivityMapPageBinding
import kr.ac.tukorea.waiter.databinding.ActivitySearchPageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchPage : AppCompatActivity() {
    private lateinit var binding: ActivitySearchPageBinding
    val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1, menu)
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val intent = Intent(this, Information_Registration_Page::class.java)

        // 리사이클러 뷰
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter

        fun searchKeyword(place_name: String) {
            //API설정
            val retrofit = Retrofit.Builder()
                .baseUrl(MapPage.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(KakaoAPI::class.java)
            val call = api.getSearchKeyword(MapPage.API_KEY, place_name)

            call.enqueue(object : Callback<ResultSearchKeyword> {
                //만약에 API와 통신성공시
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    Log.d("Test", "성공: ${response.raw()}")//로그찍기
                    Log.d("Test", "Body: ${response.body()}")//로그찍기
                    val x = response.body()?.documents?.get(0)?.x//x 확인 값
                    val y = response.body()?.documents?.get(0)?.y//y 확인 값
                    addItemsAndMarkers(response.body())
                }
                //만약에 API와 통신실패시
                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    Log.w("MainActivity", "실패 ${t.message}")
                }
            })
        }
        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
// 여기에서 변수 선언해주고 다음 페이지로 넘어가게 해주어야함
                intent.putExtra("storeName", listAdapter.itemList.get(position).name)
                intent.putExtra("roadNameAddress", listAdapter.itemList.get(position).road)
                intent.putExtra("parcelAddress", listAdapter.itemList.get(position).address)
                intent.putExtra("storeCallNum", listAdapter.itemList.get(position).phone)
                intent.putExtra("latitude_y", listAdapter.itemList.get(position).y)
                intent.putExtra("longitude_x", listAdapter.itemList.get(position).x)
                Log.d("ddddd", listAdapter.itemList.get(position).name)
                startActivity(
                    intent
                )

            }
        })
        binding.btnSearch1.setOnClickListener {
            keyword = binding.searchText1.text.toString()
            pageNumber = 1
            searchKeyword(keyword)
        }
    }
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            var posx = ""
            var posy = ""
            listItems.clear()
            Log.d("로그55", "${searchResult}")//로그 찍기
            for (document in searchResult!!.documents)
            // 해당 결과들이 documents 에 있으면
            {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.phone,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)//item에 있는내용 list로 넘기기
                listAdapter.notifyDataSetChanged()//listadapter에 변경사항 알리기
                Log.d("로그3", "${item}")//로그찍어보기
            }
        }
    }
}