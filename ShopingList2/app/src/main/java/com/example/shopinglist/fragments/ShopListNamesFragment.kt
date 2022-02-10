package com.example.shopinglist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopinglist.activities.MainApp
import com.example.shopinglist.activities.ShopListActivity
import com.example.shopinglist.databinding.FragmentShopListNamesBinding
import com.example.shopinglist.db.MainViewModel
import com.example.shopinglist.db.ShopListNameAdapter
import com.example.shopinglist.dialogs.DeletDialog
import com.example.shopinglist.dialogs.NewListDialog
import com.example.shopinglist.entities.ShopListNameItem
import com.example.shopinglist.utils.TimeManager

class ShopListNamesFragment : BaseFragment(), ShopListNameAdapter.Listener{

    private lateinit var binding: FragmentShopListNamesBinding
    private lateinit var adapter: ShopListNameAdapter

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {    // запускаем диалог и передаём сведения в Dao для записи в Bd
        NewListDialog.showDialog(activity as AppCompatActivity , object : NewListDialog.Listener{
            override fun onClick(name: String) { // name: String возвращает имя которое вписал пользователь в диалоге
                val shopListName = ShopListNameItem(
                    null,name,TimeManager.getCurrentTime(),0,0,""
                )
                mainViewModel.insertShopListName(shopListName)
            }
        }, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopListNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // инициализирует RcView
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    private fun initRcView() = with(binding){   // инициализируем адаптер
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = ShopListNameAdapter(this@ShopListNamesFragment)
        rcView.adapter = adapter
    }

    private fun observer(){ // обновлять список с данными у LiveData
        mainViewModel.allShopListNamesItem.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    companion object {  // object позволяет обращаться к функиям на прямую без инициализации (пример: FragmentManager.setFragment(NoteFragment.newInstance(),this)

        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }

    override fun deleteItem(id: Int) {
        DeletDialog.showDialog(context as AppCompatActivity, object : DeletDialog.Listener{
            override fun onClick() {
                mainViewModel.deleteShopList(id,true) // передаём id в VM полученный из ShopListNameAdapter
            }

        })
    }

    override fun editItem(shopListNameItem: ShopListNameItem) { // перезаписываем данные
        NewListDialog.showDialog(activity as AppCompatActivity , object : NewListDialog.Listener{
            override fun onClick(name: String) { // name: String возвращает имя которое вписал пользователь в диалоге
                mainViewModel.updateListName(shopListNameItem.copy(name = name)) // изменяем name для передачи в Дао
            }
        }, shopListNameItem.name)
    }

    override fun onClickItem(shopListNameItem: ShopListNameItem) { // передаём данные в ShopListActivity
        val i = Intent(activity,ShopListActivity::class.java).apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME,shopListNameItem)
        }
        startActivity(i)
    }
}