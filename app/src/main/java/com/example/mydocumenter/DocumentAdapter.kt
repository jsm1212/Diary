package com.example.mydocumenter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DocumentAdapter (private val context: Context, private val dataList: ArrayList<DataVo>) :
    RecyclerView.Adapter<DocumentAdapter.ItemViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentAdapter.ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentAdapter.ItemViewHolder, position: Int) {
        holder.bind(dataList[position], context)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userPicture = itemView.findViewById<ImageView>(R.id.pictureImage)
        private val userContent = itemView.findViewById<TextView>(R.id.contentText)
        private val userDate = itemView.findViewById<TextView>(R.id.dateText)

        fun bind(dataVo: DataVo, context: Context) {

            //사진 처리
            if (dataVo.path != "") {
                val resourceId =
                    context.resources.getIdentifier(dataVo.path, "drawable", context.packageName)

                if (resourceId > 0) {
                    userPicture.setImageResource(resourceId)
                } else {
                    Log.d("","들어옴")

                    val file: File = File(dataVo.path)
                    val bExist = file.exists()
                    if (bExist) {
                        Log.d("","이미지 파일 있음")
                        val myBitmap = BitmapFactory.decodeFile(dataVo.path)
                        userPicture.setImageBitmap(myBitmap)
                    }else{
                        Log.d("","${dataVo.path} 이미지 파일 없음")
                    }
                }
            } else {
                userPicture.setImageResource(R.mipmap.ic_launcher_round)
            }

            //TextView에 데이터 세팅
            userContent.text = dataVo.content
            userDate.text = dataVo.reg

            // 클릭시에
            itemView.setOnClickListener {
                println(dataVo.content)

                Intent(context, DetailActivity::class.java).apply {
                    putExtra("data", dataVo)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 새로운 태스크를 생성하여 그 태스크안에 엑티비티를 추가하게 됩니다.
                }.run { context.startActivity(this) }
            }


        }
    }




}