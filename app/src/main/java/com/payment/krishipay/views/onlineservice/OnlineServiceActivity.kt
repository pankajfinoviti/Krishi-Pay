package com.payment.krishipay.views.onlineservice

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.payment.iydapayment.customWebview.WebActivity
import com.payment.krishipay.R
import com.payment.krishipay.app.AppManager
import com.payment.krishipay.app.Constants
import com.payment.krishipay.databinding.ActivityOnlineService2Binding
import com.payment.krishipay.network.VolleyPostNetworkCall
import com.payment.krishipay.utill.*
import com.payment.krishipay.views.ecommarce.CommingSoon
import org.json.JSONObject

class OnlineServiceActivity : AppCompatActivity(), VolleyPostNetworkCall.RequestResponseLis {
    lateinit var binding: ActivityOnlineService2Binding
    lateinit var serviceList: ArrayList<OnlineServiceModel>
    lateinit var type: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlineService2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.extras?.getString("type").toString()

        binding.title.text = type

        networkCallUsingVolleyApi(Constants.URL.ONLINE_SERVICE, true)
    }


    private fun networkCallUsingVolleyApi(
        url: String,
        isLoad: Boolean,
    ) {
        if (AppManager.isOnline(this)) {
            VolleyPostNetworkCall(this, this, url, isLoad).netWorkCall(param())
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show()
        }
    }

    private fun param(): MutableMap<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map["type"] = type.replace(" ", "")
        map["apptoken"] = SharedPrefs.getValue(this, SharedPrefs.APP_TOKEN)
        map["user_id"] = SharedPrefs.getValue(this, SharedPrefs.USER_ID)
        return map
    }


    private fun initOnlineService(list: ArrayList<OnlineServiceModel>) {
        if (list.isNullOrEmpty()){
            startActivity(Intent( Intent(this@OnlineServiceActivity, CommingSoon::class.java)))
            finish()
        }else{
            binding.rvService.adapter = getAdapter(list, R.layout.service_list_item)
        }

    }

    private fun getAdapter(
        list: ArrayList<OnlineServiceModel>,
        layout: Int
    ): GRAdapter<OnlineServiceModel> {
        val adapter = GRAdapter(list, layout) { itemView, item, p ->
            itemView.apply {
                val tvTitle = findViewById<TextView>(R.id.serviceName)
                val serviceImg = findViewById<ImageView>(R.id.serviceImg)
                val serviceLayout = findViewById<ConstraintLayout>(R.id.serviceLayout)

                tvTitle.text = item.name
                MyUtil.setGlideImage(item.imageurl, serviceImg, context)
                serviceLayout.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("url", item.url)
                    bundle.putString("serviceType", type)
                    val intent = Intent(this@OnlineServiceActivity, WebActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }

            }
        }
        return adapter
    }

    override fun onSuccessRequest(JSonResponse: String?) {
        Print.P("Success : ${JSonResponse}")
        try {
            serviceList = ArrayList()
            val jsonObject = JSONObject(JSonResponse)
            val message = jsonObject.getString("message")
            Toast.makeText(this, "${message}", Toast.LENGTH_SHORT).show()
            if (AppHandler.checkStatus(JSonResponse, this)) {
                val dataObject = jsonObject.getJSONObject("data")
                val onlinArray = dataObject.getJSONArray("onlineservice")
                serviceList.addAll(AppHandler.parseOnlineService(onlinArray))
                initOnlineService(serviceList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFailRequest(msg: String?) {
        Toast.makeText(this, "${msg}", Toast.LENGTH_SHORT).show()
    }

}