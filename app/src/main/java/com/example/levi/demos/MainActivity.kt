package com.example.levi.demos

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.levi.demos.ui.nested.NestedActivity
import com.example.levi.demos.ui.recycler.decoration.RecyclerIndexBarActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start(RecyclerIndexBarActivity::class.java)
//        start(NestedActivity::class.java)
    }

    private fun <T : Activity> Activity.start(other: Class<T>) = this.startActivity(Intent(this, other))
}
