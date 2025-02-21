package com.example.flash.ui
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flash.R
import com.example.flash.data.DataSource

@Composable//passing instance of class FlashViewModel
fun StartScreen(modifier: Modifier,flashViewModel: FlashViewModel,
                onCategoryClicked:(Int)->Unit){
    val context= LocalContext.current
    val statuses by flashViewModel.uiState.collectAsState()// collects the values which are emitted by stateflow(uistate) and represent them as State Object
//GridCells.Adaptive(190.dp) specifies that the grid should adapt to fit as many columns as possible within the available width, where each column (cell) should be at least 190.dp wide.
    /*

    Whenever the data in _uiState changes, the uiState flow emits a new value.
Since collectAsState() is used, any change in uiState will cause statuses to be updated.
This triggers recomposition of the composables that use the statuses variable.
To summarize, yes, your understanding is correct:

statuses watches the public property uiState for any changes.
When uiState emits a new value (due to changes in _uiState), statuses gets updated.
Any composables using statuses will recompose to reflect the updated state.
     */
LazyVerticalGrid(columns = GridCells.Adaptive(128.dp), modifier = modifier,
    contentPadding = PaddingValues(10.dp),//overall spacing
    verticalArrangement = Arrangement.spacedBy(10.dp),//vertical spacing
    horizontalArrangement=Arrangement.spacedBy(10.dp)//horizontal spacing
) {
item(span={ GridItemSpan(maxLineSpan) }){
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = R.drawable.banner), contentDescription ="Banner" )
        Card(colors = CardDefaults.cardColors(containerColor = Color(108,194,111,255)),
            modifier = Modifier.fillMaxWidth().padding(5.dp)
            ) {
Text(text = "Shop by Category", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.fillMaxWidth().padding(5.dp), textAlign = TextAlign.Center)
        }
    }
}
    items(DataSource.loadCategories()){
        CategoryCard(context = context
        , stringResourceId = it.stringResourceId,
            imageResourceId = it.imageResourceId,
        flashViewModel=flashViewModel,
            onCategoryClicked = onCategoryClicked)
    }
}
}
@Composable
fun CategoryCard(context: Context,stringResourceId:Int,imageResourceId:Int,flashViewModel: FlashViewModel,onCategoryClicked:(Int)->Unit){
    val str= stringResource(id = stringResourceId)

    Card(modifier=Modifier.clickable{
        flashViewModel.updateClickText(str)
        Toast.makeText(context, "$str was clicked", Toast.LENGTH_SHORT).show()
        onCategoryClicked(stringResourceId)
    }, colors = CardDefaults.cardColors(containerColor = Color(248,221,248,255))){
        Column(modifier=Modifier.padding(10.dp)){
            Text(modifier=Modifier.width(150.dp),text = stringResource(id = stringResourceId), fontSize = 17.sp,textAlign= TextAlign.Center)
            Image(painter = painterResource(id = imageResourceId), contentDescription ="Fresh Fruits",
                modifier = Modifier.size(150.dp)
            )

        }

    }
}

