package com.websarva.wings.myownflashcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {

    lateinit var realm: Realm
    var strQuestion: String = ""  //問題
    var strAnswer: String = ""   //答え
    var intPosition: Int = 0     //行番号

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //画面が開いた時
//        WordListActivityから渡されたIntentの受け取り
        val bundle = intent.extras
        val strStatus = bundle!!.getString(getString(R.string.intent_key_status))
        textViewStatus.text = strStatus

        //修正の場合は問題と答えの表示も
        if (strStatus == getString(R.string.status_change)) {
            strQuestion = bundle.getString(getString(R.string.intent_key_question)).toString()  //問題
            strAnswer = bundle.getString(getString(R.string.intent_key_answer)).toString()      //答え
            editTextQuestion.setText(strQuestion)
            editTextAnswer.setText(strAnswer)

            intPosition = bundle.getInt(getString(R.string.intent_key_position))  //行番号

            //修正の場合は問題が修正できないようにする
            editTextQuestion.isEnabled = false
        }
        else {
            editTextQuestion.isEnabled = true
        }

        //前の画面で設定した背景色の設定
        constraintLayoutEdit.setBackgroundResource(intBackgroundColor)

        //登録ボタンを押した時
        buttonRegister.setOnClickListener {

            if (strStatus == getString(R.string.status_add)) {
                //「新しい単語を追加」の場合
                addNewWord()
            }
            else {
                //「登録した単語を修正」の場合
                changeWord()
            }

        }

        //「戻る」ボタンを押した時
        buttonBack2.setOnClickListener {
            //今の画面を閉じて単語一覧の画面に戻る
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        //Realmインスタンスの取得
        realm = Realm.getDefaultInstance()
    }

    override fun onPause() {
        super.onPause()

        //Realmインスタンスの後片付け
        realm.close()
    }

    private fun changeWord() {
        //単語の修正処理（changeWordメソッド）

        //選択した行番号のデータをDBから取得
        val results = realm.where(WordDB::class.java).findAll().sort(getString(R.string.db_field_answer))
        val selectedDB = results[intPosition]

        //単語の修正処理（changeWordメソッド）：確認ダイアログ
        val dialog = AlertDialog.Builder(this@EditActivity).apply {
            setTitle(selectedDB!!.strAnswer + "の変更")
            setMessage("変更しても宜しいですか？")
            setPositiveButton("はい") { dialog, which ->
                
                //入力した問題・答えで1のレコードを更新
                //主キー・暗記済フラグ設定に伴う変更
                realm.beginTransaction()
                //selectedDB!!.strQuestion = editTextQuestion.text.toString()
                selectedDB.strAnswer = editTextAnswer.text.toString()
                selectedDB.boolMemoryFrag = false
                realm.commitTransaction()

                //入力した文字を入力欄から消す
                editTextQuestion.setText("")
                editTextAnswer.setText("")

                //修正完了メッセージ（Toast）
                Toast.makeText(this@EditActivity, "修正が完了しました", Toast.LENGTH_LONG).show()

                //今の画面を閉じて前の画面に戻る
                finish()
            }
            setNegativeButton("いいえ") { dialog, which -> }
            show()
        }
    }

    private fun addNewWord() {

        //登録処理（addNewWord）
        val dialog = AlertDialog.Builder(this@EditActivity).apply {
            setTitle("登録")
            setMessage("登録してもよろしいですか？")
            setPositiveButton("はい") { dialog, which ->

                //単語（主キー）の重複チェック
                try {
                    //入力した問題・答えをDBに登録
                    realm.beginTransaction()  //開始処理

                    //主キー・暗記済フラグ設定に伴う変更
                    val wordDB = realm.createObject(WordDB::class.java,  editTextQuestion.text.toString())
//        wordDB.strQuestion = editTextQuestion.text.toString()
                    wordDB.strAnswer = editTextAnswer.text.toString()
                    wordDB.boolMemoryFrag = false  //暗記済にしない

                    //登録完了メッセージを表示（Toast）
                    Toast.makeText(this@EditActivity, "登録が完了しました", Toast.LENGTH_LONG).show()

                } catch (e: RealmPrimaryKeyConstraintException) {
                    //メッセージを表示（Toast）
                    Toast.makeText(this@EditActivity, "その単語は既に登録されています", Toast.LENGTH_LONG).show()

                } finally {

                    //入力した文字を入力欄から消す
                    editTextQuestion.setText("")
                    editTextAnswer.setText("")
                    realm.commitTransaction() //終了処理
                }
            }

            setNegativeButton("いいえ") { dialog, which ->  }
            show()
        }
    }
}
