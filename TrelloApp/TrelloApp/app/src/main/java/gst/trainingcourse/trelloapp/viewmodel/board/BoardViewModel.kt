package gst.trainingcourse.trelloapp.viewmodel.board

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gst.trainingcourse.trelloapp.model.List
import gst.trainingcourse.trelloapp.request.ListRequest
import gst.trainingcourse.trelloapp.view.board.BoardFragment
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class BoardViewModel: ViewModel() {

    private var listTemp = mutableListOf<List>()

    private var _list = MutableLiveData<MutableList<List>?>()
    val list: LiveData<MutableList<List>?>
        get() = _list

    fun getListTask(boardId: Int) {
        viewModelScope.launch(IO) {
            ListRequest.getListByBoard(boardId) { lists->
                Log.d("bbb", "$lists, $boardId")

                if (lists != null) {
                    _list.postValue(lists)
                    listTemp.addAll(lists)
                }
            }
        }
    }

     fun createList(listCreated: List) {
         viewModelScope.launch(IO) {
             ListRequest.createList(listCreated) { isAdded ->
                 if(isAdded) {
                     BoardFragment.boardId?.let { ListRequest.getListByBoard(it) { list ->
                         if (list != null) {
                             //listTemp.addAll(list)
                             _list.postValue(list)
                         }
                     } }
                 }
             }
         }
     }

}