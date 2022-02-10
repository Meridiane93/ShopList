package com.example.shopinglist.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.shopinglist.activities.MainApp
import com.example.shopinglist.activities.NewNoteActivity
import com.example.shopinglist.databinding.FragmentNoteBinding
import com.example.shopinglist.db.MainViewModel
import com.example.shopinglist.db.NoteAdapter
import com.example.shopinglist.entities.NoteItem


class NoteFragment : BaseFragment(),NoteAdapter.Listener {

    private lateinit var binding: FragmentNoteBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: NoteAdapter
    private lateinit var defPref: SharedPreferences

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {    // запускаем фрагмент и передаём сведения в BaseFragment об открытом фрагменте
        editLauncher.launch(Intent(activity,NewNoteActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // инициализирует RcView
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    private fun initRcView() = with(binding){   // инициализируем адаптер
        defPref = PreferenceManager.getDefaultSharedPreferences(root.context) // инициализируем SharedPreferences
        rcViewNote.layoutManager = getLayoutManager() // как печатать список ( выбираем через layoutManager )
        adapter = NoteAdapter(this@NoteFragment, defPref) // инициализировали адаптер
        rcViewNote.adapter = adapter // указываем какой адаптер будет обновлять RecyclerView
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager{ // проверяет настройки и возвращает в каком виде отображать список
        return if(defPref.getString("note_style_key","Вертикальный") == "Вертикальный" ) LinearLayoutManager(activity)
               else StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun observer(){ // обновлять список с данными у LiveData
        mainViewModel.allNotes.observe(viewLifecycleOwner, {
            adapter.submitList(it)  // обновляем список в адаптере
        })
    }

    private fun onEditResult(){   // инициализация launcher и ожидание ответа ActivityResult
        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){

                val editState = it.data?.getStringExtra(EDIT_STATE_KEY)
                if (editState == "new") mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem) // записываем в базу данных
                else mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem) // обновляем данные в БД
            }
        }
    }

    override fun deleteItem(id: Int) { // удаление через VM удаляем note_list_item по идентификатору
        mainViewModel.deleteNote(id)
    }

    override fun onClickItem(note: NoteItem) { // при нажатии на элемент передаём данные
        val intent = Intent(activity, NewNoteActivity::class.java).apply {
            putExtra(NEW_NOTE_KEY,note)
        }
        editLauncher.launch(intent)
    }

    companion object {  // object позволяет обращаться к функиям на прямую без инициализации (пример: FragmentManager.setFragment(NoteFragment.newInstance(),this)
        const val NEW_NOTE_KEY = "new_note_key"
        const val EDIT_STATE_KEY = "edit_state_key"
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}