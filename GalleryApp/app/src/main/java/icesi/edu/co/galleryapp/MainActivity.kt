package icesi.edu.co.galleryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import icesi.edu.co.galleryapp.databinding.ActivityMainBinding
import icesi.edu.co.galleryapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val viewModel:MainViewModel by viewModels()

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onGalleryResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.showUser()

        viewModel.userLD.observe(this){
            binding.textView1.text = it.name
            binding.textView2.text = it.username
            Glide.with(this).load(it.urlImage).into(binding.imageId)
        }

        binding.imageId.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            launcher.launch(intent)
        }
    }

    fun onGalleryResult(result:ActivityResult){
        val uri = result.data?.data
        Glide.with(this).load(uri).into(binding.imageId)
        uri?.let{
            viewModel.uploadImage(it)
        }

        Log.e(">>>", uri.toString())
    }
}