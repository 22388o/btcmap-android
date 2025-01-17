package map

import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import db.Place
import java.io.InputStreamReader

class BuiltInPlacesCacheImpl(
    private val assets: AssetManager,
    private val gson: Gson,
) : BuiltInPlacesCache {

    override fun loadPlaces(): List<Place> {
        val fileName = "places.json"
        val input = assets.open(fileName)
        val typeToken = object : TypeToken<List<Place>>() {}
        return gson.fromJson(InputStreamReader(input), typeToken.type)
    }
}