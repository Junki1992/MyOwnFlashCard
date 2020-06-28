package com.websarva.wings.myownflashcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*

class TestActivity : AppCompatActivity(), View.OnClickListener {

    //テスト条件（暗記済の単語を除外するか：する＝＞true）
    var boolStatusMemory: Boolean = false

    //問題を暗記済にするかどうか
    var boolMemorized: Boolean = false

    //テストの状態
    var intState: Int = 0
    val BEFORE_START: Int = 1      //テスト開始前
    val RUNNING_QUESTION: Int = 2  //問題を出した段階
    val RUNNING_ANSWER: Int = 3    //答えを出した段階
    val TEST_FINISHED: Int = 4    //現在の問題数を示すカウンター

    //realm関係
    lateinit var realm: Realm
    lateinit var results: RealmResults<WordDB>
    lateinit var word_list: ArrayList<WordDB>

    var intLength: Int = 0  //テストの問題数
    var intCount: Int = 0   //問題数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        //画面が開いた時
        //MainActivityからのIntent（テスト条件）受け取り
        val bundle = intent.extras
        boolStatusMemory = bundle!!.getBoolean(getString(R.string.intent_key_memory_frag))

        //前の画面で設定した背景色を設定
        constraintLayoutTest.setBackgroundResource(intBackgroundColor)

        //テスト状態を「開始前」に＋画像非表示
        intState = BEFORE_START
        imageViewFlashQuestion.visibility = View.INVISIBLE
        imageViewFlashAnswer.visibility = View.INVISIBLE

        //ボタン①を「テストを始める」に
        buttonNext.setBackgroundResource(R.drawable.image_button_test_start)

        //ボタン②を「かくにんテストをやめる」ボタンに
        buttonEndOfTest.setBackgroundResource(R.drawable.image_button_end_test)

        //クリックリスナー
        buttonNext.setOnClickListener(this)
        buttonEndOfTest.setOnClickListener(this)
        checkBox.setOnClickListener {
            boolMemorized = checkBox.isChecked
        }
    }

    override fun onResume() {
        super.onResume()

        //Realmインスタンスの取得
        realm = Realm.getDefaultInstance()

        //DBからテストデータを取得（テスト条件で処理分岐）
        if (boolStatusMemory) {
            //暗記済の単語を除外する
            results = realm.where(WordDB::class.java).equalTo(getString(R.string.db_field_memory_frag), false).findAll()
        }
        else {
            //暗記済の単語を除外しない（含める）
            results = realm.where(WordDB::class.java).findAll()
        }

        //残り問題数の表示
        intLength = results.size
        textViewRemaining.text = intLength.toString()

        //上記で取得したデータをシャッフル
        word_list = ArrayList(results)
        Collections.shuffle(word_list)
    }

    override fun onPause() {
        super.onPause()

        //Realmの後片付け
        realm.close()
    }

    override fun onClick(v: View) {

        when (v.id) {
            //ボタン①（上のボタン）を押した時
            //テスト状態によって処理を変える
            R.id.buttonNext ->

                when (intState) {
                    BEFORE_START -> {
                        //「テスト開始前」の場合
                        //「問題を出した段階に」＋問題表示（showQuestionメソッド）
                        intState = RUNNING_QUESTION
                        showQuestion()
                    }

                    RUNNING_QUESTION -> {
                        //「問題を出した段階」の場合
                        //「答えを出した段階」に＋答え表示（showAnswerメソッド）
                        intState = RUNNING_ANSWER
                        showAnswer()
                    }

                    RUNNING_ANSWER -> {
                        //「答えを出した段階」の場合
                        //「問題を出した段階に」＋問題表示（showQuestionメソッド）
                        intState = RUNNING_QUESTION
                        showQuestion()
                    }
                }

            R.id.buttonEndOfTest -> {
                //ボタン②（下のボタン）を押した時：かくにんダイアログ
                val dialog = AlertDialog.Builder(this@TestActivity).apply {
                    setTitle("テストの終了")
                    setMessage("テストを終了しも宜しいですか")
                    setPositiveButton("はい") { dialog, which ->
                        //テスト画面を閉じてMainActivityに戻る
                        //最後の問題の暗記済フラグをDB登録
                        if (intState == TEST_FINISHED) {
                            val selectedDB = realm.where(WordDB::class.java).equalTo(getString(R.string.db_field_question),
                                word_list[intCount -1].strQuestion).findFirst()
                            realm.beginTransaction()
                            selectedDB!!.boolMemoryFrag = boolMemorized
                            realm.commitTransaction()
                        }
                        finish()
                    }
                    setNegativeButton("いいえ") { dialog, which ->  }
                    show()
                }
            }

        }
    }

    private fun showAnswer() {
        //答え表示処理（showAnswerメソッド）
        //答えの表示（画像・文字）
        imageViewFlashAnswer.visibility = View.VISIBLE
        textViewFlashAnswer.text = word_list[intCount -1].strAnswer

        //ボタン①を「次の問題にすすむ」に
        buttonNext.setBackgroundResource(R.drawable.image_button_go_next_question)

        //最後の問題まで来たら
        if (intLength == intCount) {
            //テスト状態を「終了」にしてメッセージを表示
            intState == TEST_FINISHED
//            textViewMessage.text = "テスト終了"
            textViewMessage2.text = "テスト終了"

            //ボタン①を見えない＆使えない状態に
            buttonNext.isEnabled = false
            buttonNext.visibility = View.INVISIBLE

            //画像と文字を見えない状態に
//            imageViewFlashQuestion.visibility = View.INVISIBLE
//            imageViewFlashAnswer.visibility = View.INVISIBLE
//            textViewFlashQutstion.visibility = View.INVISIBLE
//            textViewFlashAnswer.visibility = View.INVISIBLE

            //チェックボックスを見えない状態に
            checkBox.visibility = View.INVISIBLE

            //MainActivityに戻る
//            val intent = Intent(this@TestActivity, MainActivity::class.java).apply {
//                finish()
//            }
            //ボタン②を「戻る」ボタンに
            buttonEndOfTest.setBackgroundResource(R.drawable.image_button_back)
        }
    }
    private fun showQuestion() {
        //問題表示処理（showQuestionメソッド）

        //前の問題の暗記フラグをDB登録（更新）
        if (intCount > 0) {  //2問目移行の時のみ発生
            val selectedDB = realm.where(WordDB::class.java).equalTo(getString(R.string.db_field_question),
                    word_list[intCount -1].strQuestion).findFirst()
            realm.beginTransaction()
            selectedDB!!.boolMemoryFrag = boolMemorized
            realm.commitTransaction()
        }

        //残り問題数を一つ減らして表示
        intCount ++
        textViewRemaining.text = (intLength - intCount).toString()

        //今回の問題表示と前の答えの消去（文字と画像）
        imageViewFlashAnswer.visibility = View.INVISIBLE
        textViewFlashAnswer.text = ""
        imageViewFlashQuestion.visibility = View.VISIBLE
        textViewFlashQutstion.text = word_list[intCount - 1].strQuestion

        //ボタン①を「答えを見る」ボタンに
        buttonNext.setBackgroundResource(R.drawable.image_button_go_answer)

        //問題の単語が暗記済の場合はチェックを入れる
        checkBox.isChecked = word_list[intCount -1].boolMemoryFrag
        boolMemorized = checkBox.isChecked
    }
}
