package com.example.mydocumenter

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(
    context: Context?,
    filename: String?
) : SQLiteOpenHelper(context, filename, null, 1 )
{
    // 싱글톤 설정
    companion object {
        var instance: DBHelper? = null
        fun getInstance(context: Context, fileName: String): DBHelper {
            if (instance == null) {
                instance = DBHelper(context, fileName)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        var sql: String = "CREATE TABLE if not exists myDocument (" +
                                "seq integer primary key autoincrement," +
                                "path string," +
                                "address string," +
                                "content integer," +
                                "reg TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    @SuppressLint("Range")
    fun insert(dto:DocumentDto){
        val sql = "insert into myDocument(path, address, content) " +
                " values('${dto.path}', '${dto.address}', '${dto.content}') "
        var db = this.writableDatabase
        db.execSQL(sql)

    }

    fun select():ArrayList<DataVo>{
        var list = ArrayList<DataVo>()
        var query = "SELECT * FROM myDocument"
        var db = this.writableDatabase
        var cursor = db.rawQuery(query,null)

        while(cursor.moveToNext()){
            val _seq = cursor.getColumnIndex("seq")
            val _path = cursor.getColumnIndex("path")
            val _address = cursor.getColumnIndex("address")
            val _content = cursor.getColumnIndex("content")
            val _reg = cursor.getColumnIndex("reg")

            val seq = cursor.getInt(_seq)
            val path = cursor.getString(_path)
            val address = cursor.getString(_address)
            val content = cursor.getString(_content)
            val reg = cursor.getString(_reg)

            println("$seq : $path : $address : $content : $reg")

            list.add(DataVo(seq, path, address, content, reg))
        }
        cursor.close()

        return list
    }

    fun delete(dto: DocumentDto){
        val sql = " delete from myDocument " +
                " WHERE path = '${dto.path}' "
    }

}










