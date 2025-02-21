package com.example.flash.ui
import android.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import  com.example.flash.R
import com.example.flash.data.InternetItem

//FlashAppScreen.Start.title
enum class FlashAppScreen(val title:String){
    Start("Welcome to FlashCart"),
    Items("Choose the Items"),
    Cart("Your Cart")
}
val auth=FirebaseAuth.getInstance()
//auth can be used to perform authentication. auth now has an instance of firebaseAuth class

var canNavigateBack=false
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashApp(/*modifier: Modifier,*/flashViewModel: FlashViewModel= viewModel(),
             navController:NavHostController= rememberNavController()) {
    val cartItems by flashViewModel.cartItems.collectAsState()

    val logoutClicked by flashViewModel.logoutClicked.collectAsState()

    val user by flashViewModel.user.collectAsState()
    auth.currentUser?.let { flashViewModel.setUser(it) }
/*
This property retrieves the currently authenticated user from the local cache. If a user is signed in, auth.currentUser will return a FirebaseUser object containing user details. If no user is signed in, it will return null
Yes, that’s correct. Here’s a step-by-step explanation of how this works:

 1. User Information Storage and Access

 During Sign-In
- When a user signs in (e.g., via OTP), Firebase Authentication updates its local cache with the user information. This local cache is managed by the Firebase SDK and is typically stored in persistent storage on the device.

 Setting User Information in ViewModel
- After a successful sign-in, you can set the user information in your `FlashViewModel`:
  ```kotlin
  val user = task.result?.user
  if (user != null) {
      flashViewModel.setUser(user)
  }
  ```
- This step updates your app’s state (i.e., `FlashViewModel`) with the user details. This information is local to your app and helps manage how the UI reacts to the current authentication state.

### 2. **App Restart or Recomposition**

#### Accessing User Information from Firebase Cache
- When the app is restarted or recomposed, the `FlashViewModel` might not retain the user information if it is not persisted across app restarts. However, Firebase Authentication maintains its own local cache of user information.

#### Retrieving User Information
- During app initialization, you can access the user information from Firebase’s local cache using `auth.currentUser`:
  ```kotlin
  auth.currentUser?.let { flashViewModel.setUser(it) }
  ```
- This line of code retrieves the currently authenticated user from Firebase’s local cache and updates your `FlashViewModel` with this information.

### Summary of the Flow

1. **Sign-In Process**:
   - User signs in, Firebase updates its local cache.
   - `FlashViewModel` is updated with user information.

2. **App Restart**:
   - `FlashViewModel` does not automatically retain user information unless explicitly saved and restored.

3. **Firebase Cache Retrieval**:
   - `auth.currentUser` retrieves user information from Firebase’s local cache.
   - This information is used to update `FlashViewModel` when the app starts or when needed.

### Why This Approach Is Useful

- **Consistency**: By accessing `auth.currentUser` upon app restart, you ensure that your app has the correct and most recent authentication state, even if the `FlashViewModel` was reset or lost its state.
- **User Experience**: This approach helps maintain a seamless user experience by automatically signing in users and restoring their session without requiring them to log in again after restarting the app.

So, in summary, Firebase Authentication’s local cache ensures that user information is available even if the app's state is lost between sessions, and `auth.currentUser` helps restore this state in your `FlashViewModel`.
 */
    val backStackEntry by navController.currentBackStackEntryAsState()//gives info about current screen/about screens in the backstack
    val currentScreen = FlashAppScreen.valueOf(
        backStackEntry?.destination?.route ?: FlashAppScreen.Start.name
    )//extracts route (a string identifier)  of the current destination from backStackEntry.
    /*for eg:- val currentScreen=FlashAppScreen.Items
    now we will be able to access the title property by using the variable currentScreen i.e. currentScreen.title

    .valueOf() function is a static method that lets you convert a string representation of an enum constant into the actual enum constant itself.
    the variable currentScreen indeed holds a reference to the actual enum constant from your FlashAppScreen enum.
    */
    canNavigateBack = navController.previousBackStackEntry != null
    val isVisible by flashViewModel.isVisible.collectAsState()
    if (isVisible) {
        OfferScreen()

    }
    else if(user==null){
        //if(user==null) this means that user is not logged in
        LoginUi(flashViewModel = flashViewModel)
    }
    else {
        Scaffold(modifier = Modifier.fillMaxWidth(),
            topBar = {


                        TopAppBar(
                            modifier = Modifier.windowInsetsPadding(
                                WindowInsets.statusBars.only(
                                    WindowInsetsSides.Top
                                )
                            )
                            //        , colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Green)

                            , title = {
                                Row {

                                    if(currentScreen==FlashAppScreen.Cart){
                                        Text(
                                            text ="${currentScreen.title}(${cartItems.size})",
                                            Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            color = Color.Black,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }else{
                                        Text(
                                            text = currentScreen.title,
                                            Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            color = Color.Black,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            },

                            navigationIcon = {
                                if (canNavigateBack) {
                                    IconButton(onClick = { navController.navigateUp() })
                                    {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back Button"
                                        )

                                    }
                                }
                            }, actions = {Row(modifier = Modifier
                                .clickable {
                                    flashViewModel.setLogoutStatus(true)
                                    /* auth.signOut()
                            used to logout the current user but the login screen will not be shown because auth.signOut()
                            does not set the user variable to null. we have to make a function to set the variables associated
                            with the user to null so that login screen shows up.

                            */
                                    //flashViewModel.clearData()
                                }
                                .padding(end = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                                Icon(painter = painterResource(id = R.drawable.logout), contentDescription ="Logout", modifier = Modifier.size(25.dp) )
                                Text(text = "Logout", fontSize = 14.sp)}}


                        )






            }, bottomBar = { FlashAppBar(navController = navController, currentScreen = currentScreen, cartItems = cartItems) }


        ) { innerPadding ->
            NavHost(navController = navController, startDestination = FlashAppScreen.Start.name) {
                composable(route = FlashAppScreen.Start.name) {
                    StartScreen(
                        modifier = Modifier.padding(innerPadding),
                        flashViewModel = flashViewModel,
                        onCategoryClicked = {
                            flashViewModel.updateSelectedCategory(it)
                            navController.navigate(FlashAppScreen.Items.name)
                        })
                }
                composable(route = FlashAppScreen.Items.name) {

                    InternetItemsScreen(
                        modifier = Modifier.padding(innerPadding),
                        flashViewModel = flashViewModel,
                        itemUiState = flashViewModel.itemUiState
                    )


                }
                composable(route=FlashAppScreen.Cart.name){
                    CartScreen(flashViewModel = flashViewModel, modifier = Modifier.padding(innerPadding), onHomeButtonClicked = {navController.navigate(FlashAppScreen.Start.name){popUpTo(0)} })
                }
            }
        }
        if(logoutClicked){
            AlertCheck(onYesButtonPressed = {
                flashViewModel.setLogoutStatus(false)
                auth.signOut()
                flashViewModel.clearData() }, onNoButtonPressed = {flashViewModel.setLogoutStatus(false)})



        }

    }}
    //viewModel() obtains the current instance of the View Model (if exists) or creates a new one if it does not exist
//It either creates a new instance of the ViewModel if one does not already exist or retrieves an existing one if it has been previously created.
    @Composable
    fun FlashAppBar(modifier: Modifier = Modifier, navController: NavHostController,
                    currentScreen: FlashAppScreen,cartItems:List<InternetItem>) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                .fillMaxWidth()
                .padding(horizontal = 70.dp, vertical = 10.dp)

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp),
                modifier = Modifier.clickable {
                    navController.navigate(FlashAppScreen.Start.name) {
                        popUpTo(0)//clears all the screen in the backstack
                    }
                }) {
                Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home")
                Text(text = "Home", fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp),
                modifier = Modifier.clickable {
                    if(currentScreen!=FlashAppScreen.Cart) {
                        navController.navigate(FlashAppScreen.Cart.name)
                    }
                }) {
                Box {
                    Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = "Cart")
                    if(cartItems.isNotEmpty())
                    {Card(modifier=Modifier.align(Alignment.TopEnd), colors = CardDefaults.cardColors(containerColor = Color.Red)) {
                        Text(text = "${cartItems.size}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 1.dp))
                    }
                }}
                Text(text = "Cart", fontSize = 10.sp)
            }
        }
    }
//if(currentScreen!=FlashAppScreen.Cart) if we dont write this then if we click the cart button 5 times then it will be opened for 5 times
//and we would have to press back button 5 times to go back to the previous screen
@Composable
fun AlertCheck(
    onYesButtonPressed:()->Unit,
    onNoButtonPressed:()->Unit
) {
    AlertDialog(

        title = {
            Text(text = "Logout?", fontWeight = FontWeight.Bold)
        },
        containerColor = Color.White,
        text = {
            Text(text = "Are you sure you want to Logout?")
        },
        confirmButton = {
            TextButton(onClick = { onYesButtonPressed() }) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = { onNoButtonPressed() }) {
                Text(text = "No")
            }

        },
        onDismissRequest = {
            onNoButtonPressed()
        }

    )
}
