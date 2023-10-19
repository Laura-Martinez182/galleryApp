package icesi.edu.co.galleryapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class MainViewModel : ViewModel() {
    val userLD = MutableLiveData<User>();

    fun uploadImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            //Cargar la imagen
            try {
                val uuid = UUID.randomUUID().toString()
                Firebase.storage.reference
                    .child("profileImages")
                    .child("apps")
                    .child("232")
                    .child(uuid)
                    .putFile(uri).await()

                Firebase.firestore.collection("users")
                    .document("ffT9xnanmbelidCkLDh6RqApDKv1")
                    .update("photoID", uuid).await()

            } catch (e: Exception) {
                Log.e(">>>", e.message.toString())

            }
        }
    }

    fun showUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val doc = Firebase.firestore.collection("users")
                .document("ffT9xnanmbelidCkLDh6RqApDKv1")
                .get()
                .await()
            val user = doc.toObject(User::class.java)
            Log.e(">>>", user?.photoID.toString())

            user?.let {
                val url = Firebase.storage.reference
                    .child("profileImages")
                    .child("apps")
                    .child("232")
                    .child(it.photoID).downloadUrl.await()
                Log.e(">>>", url.toString())

                val localUser = User(
                    it.id,
                    it.name,
                    it.photoID,
                    it.username,
                    url.toString()
                )

                withContext(Dispatchers.Main) {
                    userLD.value = localUser
                }
            }
        }
    }
}

data class User(
    var id: String = "",
    var name: String = "",
    var photoID:String = "",
    var username: String = "",
    var urlImage:String? = null

)