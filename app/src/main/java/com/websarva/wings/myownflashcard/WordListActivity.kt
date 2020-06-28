package com.websarva.wings.myownflashcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_word_list.*

class WordListActivity : AppCompatActivity(), AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {

    lateinit var realm: Realm
    lateinit var results: RealmResults<WordDB>
    lateinit var word_list: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)

        //画面が開いた時

        //全画面で設定した背景色を設定
        constraintLayoutWordList.setBackgroundResource(intBackgroundColor)

        //ボタンのクリック処理
        //「新しい単語の追加」ボタンを押した場合
        //「EditActivity」へ（ステータスをIntentで渡す）
        buttonAddNewWord.setOnClickListener {
            val intent = Intent(this@WordListActivity, EditActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_status), getString(R.string.status_add))
            startActivity(intent)
        }

        //「戻る」ボタンを押した場合
        //今の画面を閉じてMainActivityへ
        buttonBack.setOnClickListener {
            finish()
        }

            //暗記済は下に」ボタンを押した場合
            //暗記済フラグが立っているものを下にソート
            buttonSort.setOnClickListener {
                results = realm.where<WordDB>(WordDB::class.java).findAll().sort(getString(R.string.db_field_memory_frag))

                //一旦表示しているword_listをクリア（下に重なって表示されるのを防ぐため）
                word_list.clear()

                results.forEach {
                    if (it.boolMemoryFrag) {
                        word_list.add(it.strAnswer + ":" + it.strQuestion + "【暗記済】")
                    }
                    else {
                        word_list.add(it.strAnswer + " : " + it.strQuestion)
                    }
                }
                listView.adapter = adapter
            }


        //リストのクリックリスナー
        listView.setOnItemClickListener(this)
        listView.setOnItemLongClickListener(this)

    }

    override fun onResume() {
        super.onResume()

        //Realmインスタンスの取得
        realm = Realm.getDefaultInstance()

        //DBに登録している単語を一覧表示（ListView）
        results = realm.where(WordDB::class.java).findAll().sort(getString(R.string.db_field_answer))

        //for文を使ってリストの表示形式を修正する
        //暗記済のものは「【暗記済】」と表示
        word_list = ArrayList<String>()  //表示形式を修正したリスト
        val length = results.size
//        for (i in 0..length -1) {
//            if (results[i]!!.boolMemoryFrag) {
//                word_list.add(results[i]!!.strAnswer + ":" + results[i]!!.strQuestion + "【暗記済】")
//            }
//            else {
//                word_list.add(results[i]!!.strAnswer + ":" + results[i]!!.strQuestion)
//            }
//        }
        results.forEach {
            if (it.boolMemoryFrag) {
                word_list.add(it.strAnswer + ":" + it.strQuestion + "【暗記済】")
            }
            else {
                word_list.add(it.strAnswer + " : " + it.strQuestion)
            }
        }

        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, word_list)
        listView.adapter = adapter
    }

    override fun onPause() {
        super.onPause()

        //Realmインスタンスの後片付け
        realm.close()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //リスト内の単語をタップした場合
        //タップした項目をDBから取得
        val selectedDB = results[position]!!
        val strSelectedQuestion = selectedDB.strQuestion
        val strSelectedAnswer = selectedDB.strAnswer

        //EditActivity（単語帳画面）を開く
        //ステータスをIntentで渡す
        val intent = Intent(this@WordListActivity, EditActivity::class.java).apply {
            putExtra(getString(R.string.intent_key_question), strSelectedQuestion)  //問題
            putExtra(getString(R.string.intent_key_answer), strSelectedAnswer)  //答え
            putExtra(getString(R.string.intent_key_position), position)  //行番号
            putExtra(getString(R.string.intent_key_status), getString(R.string.status_change))  //ステータス
        }
        startActivity(intent)
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {

        //長押しした項目をDBから取得
        val selectedDB = results[position]!!

        //リスト内の単語を長押しした場合（確認ダイアログの表示）
        val dialog = AlertDialog.Builder(this@WordListActivity).apply {
            setTitle(selectedDB.strAnswer + "の削除")
            setMessage("削除してもよろしいですか？")
            setPositiveButton("はい") { dialog, which ->
                //取得した内容をDBから削除
                realm.beginTransaction()
                selectedDB.deleteFromRealm()
                realm.commitTransaction()

                //取得した内容を一覧（リスト）から削除
                word_list.removeAt(position)

                //DBから単語帳データを再取得して表示
                listView.adapter = adapter
            }
            setNegativeButton("いいえ") { dialog, which ->
            }
            show()
        }
        return true
    }
}
