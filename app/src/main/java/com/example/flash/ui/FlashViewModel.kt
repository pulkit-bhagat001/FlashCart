package com.example.flash.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flash.data.InternetItem
import com.example.flash.network.FlashApi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
The _uiState is updated first when you change its value in the ViewModel.
The uiState property reflects the updated value immediately because it is essentially a read-only view of _uiState.
Observers of uiState, such as composables, will receive the updated value and recompose as necessary.
 */


class FlashViewModel(application:Application): AndroidViewModel(application) {        //     it
    private val _uiState =
        MutableStateFlow(FlashUiState()) // When you write MutableStateFlow(FlashUiState()), you are creating a MutableStateFlow that holds an instance of FlashUiState
    val uiState: StateFlow<FlashUiState> = _uiState.asStateFlow()

    val _isVisible = MutableStateFlow<Boolean>(true)
    val isVisible = _isVisible

    private val _cartItems = MutableStateFlow<List<InternetItem>>(emptyList())
    val cartItems: StateFlow<List<InternetItem>> get() = _cartItems.asStateFlow()

    private val Context.dataStore:DataStore<Preferences> by preferencesDataStore(name = "cart")


    private val context = application.applicationContext
    private val cartItemsKey= stringPreferencesKey("cart_items")

    private suspend fun saveCartItemsToDataStore(){
        context.dataStore.edit {
            preferences->
            preferences[cartItemsKey]= Json.encodeToString(_cartItems.value)
        }
    }
private suspend fun loadItemsFromDataStore(){
    val fullData=context.dataStore.data.first()
    val cartItemsJson=fullData[cartItemsKey]
    if(!cartItemsJson.isNullOrEmpty()){
        _cartItems.value=Json.decodeFromString(cartItemsJson)
    }
}


    fun addToCart(item:InternetItem){
        _cartItems.value+=item
        viewModelScope.launch {
            saveCartItemsToDataStore()
        }
    }
    fun removeFromCart(oldItem:InternetItem){
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var itemRemoved=false

                for(childSnapshot in dataSnapshot.children){
                    val item=childSnapshot.getValue(InternetItem::class.java)
                    item?.let {
                        if(oldItem.itemName.lowercase()==it.itemName.lowercase()&&oldItem.itemPrice==it.itemPrice){
                                childSnapshot.ref.removeValue()
                            itemRemoved=true
                        }



                    }
                    if(itemRemoved)  break
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    val database = Firebase.database
    val myRef = database.getReference("users/${auth.currentUser?.uid}/cart")
    fun addToDatabase(item:InternetItem) {
        myRef.push().setValue(item)
    }
    fun fillCartItems() {
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _cartItems.value= emptyList()
                for(childSnapshot in dataSnapshot.children){
                    val item=childSnapshot.getValue(InternetItem::class.java)
                    item?.let {
                        val newItem=it
                        addToCart(newItem)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    // holding the user information
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: MutableStateFlow<FirebaseUser?> get() = _user

    fun setUser(user: FirebaseUser){
        _user.value=user
    }

    fun clearData(){
        _user.value=null
        _phoneNumber.value=""
        _otp.value=""
        _verificationId.value=""
        resetTimer()
    }
//long is used to store date and time information due to its higher precision
    private val _ticks=MutableStateFlow(60L)// here 60L means 60 seconds
    val ticks:MutableStateFlow<Long> = _ticks

    fun runTimer(){
     timerJob=   viewModelScope.launch {
             while(_ticks.value>0){
                 delay(1000)
                 _ticks.value-=1
             }
        }
    }
    fun resetTimer(){
        try {//50-50 chance of an error
            timerJob.cancel()
        }catch (_:Exception){

        }finally {
            _ticks.value=60L
        }
    }
private lateinit var timerJob: Job


    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: MutableStateFlow<String> get() = _phoneNumber

    private val _verificationId=MutableStateFlow("")
    val verificationId:MutableStateFlow<String>get()  = _verificationId
    fun setVerificationId(verificationId:String){
        Log.d("FlashViewModel", "Verification ID updated: $verificationId")
        _verificationId.value=verificationId
    }

    private val _otp=MutableStateFlow("")
    val otp:MutableStateFlow<String> = _otp

    fun setOtp(otp:String){
        _otp.value=otp
    }

    private val _loading=MutableStateFlow<Boolean>(false)
    val loading: MutableStateFlow<Boolean> =_loading
 fun setLoading(isLoading:Boolean){
     _loading.value=isLoading
 }

    private val _logoutClicked=MutableStateFlow<Boolean>(false)
    val logoutClicked: MutableStateFlow<Boolean> =_logoutClicked
    fun setLogoutStatus(logoutStatus:Boolean){
        _logoutClicked.value=logoutStatus
    }

    var itemUiState: ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set               // mutableStateOf(ItemUiState.Success(listResult))

    //When you access itemUiState, you're actually accessing the value held by the MutableState object, which is initially the ItemUiState.Loading instance.
//When you assign ItemUiState.Success(listResult) to itemUiState, itemUiState will hold an instance of the ItemUiState.Success data class with items set to listResult.
    lateinit var internetJob: Job
    lateinit var screenJob: Job
    /*
if i want the ui to be updated only for 1 composable then mutableStateOf() should be used and if there are
 multiple composables that needs to be updated then MutableStateFlow() should be used
 */


    /*
    In Kotlin, private set is used to make the setter of a property private while keeping the getter public.
    This means that the property can be read from anywhere, but it can only be modified within the class where
    it is defined.
     */


    /*
       The value inside mutableStateOf will be updated to newItem.
   The itemUiState variable will reflect this new value.
   Any Composables that read itemUiState will be recomposed to reflect the change
        */

    sealed interface ItemUiState {
        data class Success(val items: List<InternetItem>) : ItemUiState
        object Loading : ItemUiState
        object Error : ItemUiState
    }//uiState typically refers to an object or a data class that holds the state of the UI.
    // It represents the data that the UI displays and reacts to.

    fun updateClickText(updatedText: String) {
        _uiState.update { n: FlashUiState ->
            n.copy(clickStatus = updatedText)
        }

    }

    fun updateSelectedCategory(updatedCategory: Int) {
        _uiState.update { it.copy(selectedCategory = updatedCategory) }
    }

    fun toggleVisibility() {
        _isVisible.value = false
    }

    init {//coroutine for displaying offer screen(splash screen)
        screenJob = viewModelScope.launch(Dispatchers.Default) {
            delay(3000)
            toggleVisibility()
        }
        getFlashItems()//this will call the function automatically when the app opens so that data gets downloaded in the background
        fillCartItems()
        /*
so when init is executed , it encounters a coroutine. when the coroutine starts executing at the same time
getFlashItems() is called as the coroutine runs on background thread and runs concurrently. so both are run
simultaneously and if the internet is there then there will be delay  of 3000 ms and if there is no internet then
the coroutine in the init will get cancelled


       init{
       function1call()
       function2call()
            screenJob= viewModelScope.launch(Dispatchers.Default) {
            delay(3000)
            toggleVisibility()
            }

           function3call()
           }

            in this first function1call() will be executed and when it completes
           then function2call() will be executed and when it is completed then the coroutine will be launched in the
           background and the function3call() starts executing immediately after the coroutine is launched.
           This happens without waiting for the coroutine to complete or to start its execution.
 */

    }

    // try catch block is used for exception handling
    fun getFlashItems() {
        internetJob = viewModelScope.launch {//name given to this coroutine
            try {//internet connection available
                val listResult: List<InternetItem> = FlashApi.retrofitService.getItems()
                itemUiState = ItemUiState.Success(listResult)
                loadItemsFromDataStore()
            } catch (exception: Exception) {//internet connection not available(airplane mode)
                itemUiState = ItemUiState.Error
                toggleVisibility()
                screenJob.cancel()

            }

        }
    }
    /*
    fetching data from the internet happens in the background.
    so we can use coroutine( background thread)(default dispatcher runs on a shared pool of background threads)
     */

//copy is a function provided by Kotlin's data class that creates a new instance of the data class with some properties modified.
//Here, it.copy(clickStatus = updatedText) creates a new FlashUiState instance where the clickStatus property is updated to the value of updatedText.
//The copy function is useful for creating immutable updates: instead of modifying the existing FlashUiState, a new instance is created with the updated values.
//The current value of _uiState is the FlashUiState instance that _uiState currently holds.(referred as it in lambda)

    /*
Result:

After the update call, _uiState is updated with a new FlashUiState instance where clickStatus has been set to updatedText.
This update triggers any observers of uiState (e.g., composables using collectAsState()) to react to the state change and recompose if needed.
 */

    /*
uiState is a read-only view of _uiState created by calling asStateFlow(), which means it always holds the current value of _uiState.

so we are extending to ViewModel() so that only this
function can change the state of the instance which
handles security?
ChatGPT
Yes, by extending ViewModel and using a private mutable
 state (like MutableStateFlow), you ensure that only the
  ViewModel can change the state. This encapsulation
  pattern promotes security and predictability in your
  app's state management.
 */

    fun setPhoneNumber(phoneNumber: String){
        _phoneNumber.value=phoneNumber
    }





}
