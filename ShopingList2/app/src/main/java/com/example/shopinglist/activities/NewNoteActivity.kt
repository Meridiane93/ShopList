package com.example.shopinglist.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.shopinglist.R
import com.example.shopinglist.databinding.ActivityNewNoteBinding
import com.example.shopinglist.entities.NoteItem
import com.example.shopinglist.fragments.NoteFragment
import com.example.shopinglist.utils.HtmlManager
import com.example.shopinglist.utils.MyTouchListener
import com.example.shopinglist.utils.TimeManager
import java.util.*

class NewNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewNoteBinding
    private lateinit var defPref: SharedPreferences
    private var note: NoteItem ?= null // для проверки ( что мы получаем из NoteFragment
    private var pref: SharedPreferences ?= null // инициализировать объект
    private var currentTheme = "" // сохраняется Theme которая сейчас


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        defPref = PreferenceManager.getDefaultSharedPreferences(this) // инициализируем SharedPreferences
        currentTheme = defPref.getString("theme_key", "green").toString() // инициализируем currentTheme
        setTheme(getSelectedTheme())
        setContentView(binding.root)
        actionBarSettings()
        init() // сначало инициализируем preferences
        setTextSize() // берём значения из preferences
        getNote()
        onClickColorPicker()
    }

    // слушатель нажатий для выбора цвета в colorPicker
    private fun onClickColorPicker() = with(binding){
        ibRed.setOnClickListener { setColorForSelectedText(R.color.picker_red) }
        ibBlue.setOnClickListener { setColorForSelectedText(R.color.picker_blue) }
        ibGreen.setOnClickListener { setColorForSelectedText(R.color.picker_green) }
        ibNeut.setOnClickListener { setColorForSelectedText(R.color.picker_neut) }
        ibOrange.setOnClickListener { setColorForSelectedText(R.color.picker_orange) }
        ibYellow.setOnClickListener { setColorForSelectedText(R.color.picker_yellow) }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(){   // присвоили в слушатель colorPicker передвижение палитры цветов
        binding.colorPicker.setOnTouchListener(MyTouchListener())
        pref = PreferenceManager.getDefaultSharedPreferences(this) // инициализируем pref и получаем доступ к настройкам DefaultSharedPreferences откуда и будем доставать выбранные настройки пользователем
    }

    private fun getNote(){ // получение NoteItem из NoteFragment и проверка на null

        val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if (sNote != null){
            note = sNote as NoteItem
            fillNote() // проверка на null  NoteItem-а
        }
    }

    private fun fillNote() = with(binding){ // заполняем note_list_item
        edTitle.setText(note?.title)
        edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // добавление трёх точек в которой save
        menuInflater.inflate(R.menu.new_note_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // клики на выбранные эелементы в меню (к примеру: save)
        when (item.itemId) {
            R.id.id_save ->  setMainResult() // отправляем интент в NoteFragment c новой созданной заметкой
            R.id.id_bold ->  setBoldForSelectedText()
            R.id.id_color -> if(binding.colorPicker.isShown) closeColorPicker() else openColorPicker()
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText() = with(binding) { // настройка и проверка выделенного текста на выделенный шрифт
        val startPos = edDescription.selectionStart // начало выделенного текста
        val endPos = edDescription.selectionEnd // конец выделенного текста

        val styles = edDescription.text.getSpans(startPos,endPos, StyleSpan::class.java) //  показывает сколько стилей есть в edDescription от startPos до endPos если есть
        var boldStyle: StyleSpan ?= null

        if(styles.isNotEmpty()){
            edDescription.text.removeSpan(styles[0])
        } // если есть стиль, то мы его убираем через removeSpan
        else{
            boldStyle = StyleSpan(Typeface.BOLD)
        } // если нет, добавляем

        edDescription.text.setSpan(boldStyle,startPos,endPos,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE тип добавления
        edDescription.text.trim() // удаляет пробелы html
        edDescription.setSelection(startPos) // чтобы курсор был в начале слова который мы выбрали
    }

    private fun setColorForSelectedText(colorId: Int) = with(binding) { // окрашивание текста
        val startPos = edDescription.selectionStart // начало выделенного текста
        val endPos = edDescription.selectionEnd // конец выделенного текста

        val styles = edDescription.text.getSpans(startPos,endPos, ForegroundColorSpan::class.java) //  показывает есть ли цвет в edDescription от startPos до endPos

        if(styles.isNotEmpty()){ // если есть стиль, то мы его убираем через removeSpan
            edDescription.text.removeSpan(styles[0])
        }

        edDescription.text.setSpan(ForegroundColorSpan(
            ContextCompat.getColor(this@NewNoteActivity, colorId)),startPos,endPos,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // добавляет цвет на текст
        edDescription.text.trim() // удаляет пробелы html
        edDescription.setSelection(startPos) // чтобы курсор был в начале слова который мы выбрали
    }

    private fun setMainResult(){ // создаём интент, заполняем его

        var editState = "new" // передадим в NoteFragment, для определения, обновить нам старую заметку или создать новую

        val tempNote: NoteItem? = if(note == null) createNewNote() // проверка NoteItem на null, если пусто то вернём созданную новую заметку спомощью createNewNote()
        else{
            editState = "update"
            updateNote()// или вернём обновлённую заметку
        }

        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK,i)
        finish()
    }

    private fun updateNote(): NoteItem ?= with(binding){ // перезаписываем наш note_list_item
        note?.copy(title = edTitle.text.toString(), content = HtmlManager.toHtml(edDescription.text))
    }

    private fun createNewNote(): NoteItem {
         return NoteItem( // заполняем полностью класс данными
            null, binding.edTitle.text.toString(), HtmlManager.toHtml(binding.edDescription.text), TimeManager.getCurrentTime(), "")
    }

    private fun actionBarSettings(){ // кнопка возврата
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openColorPicker(){ // открывает анимацию у выбора цвета
        binding.colorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this,R.anim.open_color_picker) // загружаем анимацию
        binding.colorPicker.startAnimation(openAnim) // добавляем анимацию в colorPicker
    }

    private fun closeColorPicker(){ // закрывает анимацию у выбора цвета
        val closeAnim = AnimationUtils.loadAnimation(this,R.anim.close_color_picker) // загружаем анимацию
        closeAnim.setAnimationListener(object : Animation.AnimationListener{

            override fun onAnimationStart(p0: Animation?) { } // когда запускается анимация

            override fun onAnimationEnd(p0: Animation?) {  // когда закрывается анимация
                binding.colorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animation?) { } // если она повторяется
        })
        binding.colorPicker.startAnimation(closeAnim) // добавляем анимацию в colorPicker
    }

    private fun setTextSize() = with(binding){ // назначение размера текста для EditText
        edTitle.setTextSize(pref?.getString("title_size_key","16")) // назначаем edTitle размер выбранный из настроек, берём из pref строку по ключу title_size_key и значение по умолчанию 16
        edDescription.setTextSize(pref?.getString("content_size_key","14")) // назначаем edDescription размер выбранный из настроек, берём из pref строку по ключу content_size_key и значение по умолчанию 14
    }

    private fun EditText.setTextSize(size: String?){ // у EditText нет назначения размера текста, добавили в EditText свою функцию
        if(size != null) this.textSize = size.toFloat() // если EditText не пуст, то EditText.textSize равен(можно) назначать размер
    }

    private fun getSelectedTheme():Int{ // определяем какая тема была выбрана
        return if (defPref.getString("theme_key", "green") == "green") R.style.Theme_NewNoteBlue
        else R.style.Theme_NewNoteRed
    }
}