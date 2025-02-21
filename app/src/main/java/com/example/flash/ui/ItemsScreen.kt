package com.example.flash.ui
import com.example.flash.R
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flash.data.InternetItem

@Composable
fun ItemsScreen(modifier:Modifier,flashViewModel: FlashViewModel,
                items:List<InternetItem>
){  val flashUiState by flashViewModel.uiState.collectAsState()

    val selectedCategory= stringResource(id = flashUiState.selectedCategory)
    val context= LocalContext.current
    val database=items.filter { it.itemCategory.lowercase()== selectedCategory.lowercase()}

    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {item(span={ GridItemSpan(2) }){
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
            Image(painter = painterResource(id = R.drawable.banner), contentDescription ="Banner" )
            Card(colors = CardDefaults.cardColors(containerColor = Color(108,194,111,255)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(text = "$selectedCategory(${database.size})", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp), textAlign = TextAlign.Center)
            }
        }
    }
        items(database){

            ItemsCard(
                stringResourceId = it.itemName,
                imageResourceId = it.imageUrl,
                itemQuantity = it.itemQuantity,
                itemPrice = it.itemPrice,
                context =context,
                flashViewModel=flashViewModel
            )
        }
    }
}

@Composable
fun InternetItemsScreen(modifier: Modifier,itemUiState: FlashViewModel.ItemUiState,flashViewModel: FlashViewModel) {

when(itemUiState){
    is FlashViewModel.ItemUiState.Loading -> {
        LoadingScreen()
    }
    is FlashViewModel.ItemUiState.Success -> {
        ItemsScreen(modifier = modifier, flashViewModel = flashViewModel, items = itemUiState.items)
    }
    else -> {
        ErrorScreen(flashViewModel = flashViewModel)
    }
}
}

@Composable
fun ItemsCard(
    stringResourceId: String,
    imageResourceId: String,
    itemQuantity:String,
    itemPrice:Int,
    context: Context,
    flashViewModel: FlashViewModel) {
    Column(Modifier.width(150.dp)) {
        Card( colors = CardDefaults.cardColors(containerColor = Color(248,221,248,255))) {
            Box {
                AsyncImage(
                    model=imageResourceId,
                    contentDescription = stringResourceId
                    ,
                    Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .padding(10.dp)
                )
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.End) {
                    Card(modifier = Modifier,colors = CardDefaults.cardColors(containerColor = Color(244,67,54,255))) {
                        Text(text = "25% off",color=Color.White,fontSize=9.sp, modifier = Modifier.padding(horizontal = 5.dp))
                    }
                }
            }
           
        }
        Text(text = stringResourceId, fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth(),
            maxLines = 1,
            textAlign = TextAlign.Left

            )

        Row(modifier = Modifier
            .fillMaxWidth()
           ,horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom,) {
           Column(verticalArrangement = Arrangement.spacedBy((-11).dp)){
               Text(text = "Rs.$itemPrice", fontSize = 6.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough, textAlign = TextAlign.Center, maxLines = 1)

               Text(text = "Rs.${itemPrice*75/100}", fontSize = 10.sp, color = Color(244,67,54,255), textAlign = TextAlign.Center, maxLines = 1)

           }
            Text(text = itemQuantity, fontSize = 14.sp, color = Color.Gray)
        }
        Card(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)
            .clickable {
                Toast
                    .makeText(context, "Added to Cart", Toast.LENGTH_SHORT)
                    .show()
                flashViewModel.addToDatabase(InternetItem(itemName = stringResourceId, itemQuantity=itemQuantity, itemPrice = itemPrice, imageUrl = imageResourceId, itemCategory = ""))
            }, colors = CardDefaults.cardColors(containerColor = Color(108,194,111,255))) {
            Text(text = "Add to Cart", modifier = Modifier.fillMaxWidth()

                , color = Color.White, textAlign = TextAlign.Center, fontSize = 12.sp)

        }
    }

}

@Composable
fun LoadingScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
        Image(painter = painterResource(id = R.drawable.loading), contentDescription = "Loading")
    }
}

@Composable
fun ErrorScreen(flashViewModel: FlashViewModel) {
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = R.drawable.error), contentDescription = "Error")
        Text(text = "Oops! Internet unavailable. Please check your internet connection", modifier = Modifier.fillMaxWidth(), color = Color.Black, textAlign = TextAlign.Center)
        Button(onClick = { flashViewModel.getFlashItems() }) {
            Text(text = "Retry")
        }
    }
}