package com.example.flash.data
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.example.flash.R


object DataSource {
fun loadCategories():List<Categories>{
return listOf<Categories>(
    Categories(stringResourceId = R.string.fresh_fruits, imageResourceId = R.drawable.fruits),
    Categories(stringResourceId = R.string.bath_body, imageResourceId = R.drawable.bath),
    Categories(stringResourceId = R.string.bread_biscuits, imageResourceId = R.drawable.bread),
    Categories(stringResourceId = R.string.kitchen_essentials, imageResourceId = R.drawable.kitchen),
    Categories(stringResourceId = R.string.munchies, imageResourceId = R.drawable.munchies),
    Categories(stringResourceId = R.string.packaged_food, imageResourceId = R.drawable.packaged_food),
    Categories(stringResourceId = R.string.stationery, imageResourceId = R.drawable.stationary),
    Categories(stringResourceId = R.string.pet_food, imageResourceId = R.drawable.dog_food),
    Categories(stringResourceId = R.string.sweet_tooth, imageResourceId = R.drawable.ice_cream),
    Categories(stringResourceId = R.string.vegetables, imageResourceId = R.drawable.vegetables),
    Categories(stringResourceId = R.string.beverages, imageResourceId = R.drawable.beverages)
)
}
    fun loadItems(
        @StringRes categoryName:Int
    ):List<Item>{
        return listOf<Item>(
            Item(R.string.banana_robusta,R.string.fresh_fruits,"1 kg",100,R.drawable.banana),
            Item(R.string.shimla_apple,R.string.fresh_fruits,"1 kg",250,R.drawable.apple),
            Item(R.string.papaya_semi_ripe,R.string.fresh_fruits,"1 kg",150,R.drawable.papaya),
            Item(R.string.pomegranate,R.string.fresh_fruits,"500 gm",150,R.drawable.pomegranate),
            Item(R.string.pineapple,R.string.fresh_fruits,"1 kg",130,R.drawable.pineapple),
            Item(R.string.pepsi_can,R.string.beverages,"1",40,R.drawable.pepsi)
        ).filter { categoryName==it.itemCategoryId }
    }
}