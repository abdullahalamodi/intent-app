package com.abdullahalamodi.implictintentapp

import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.WRITE_CONTACTS
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

const val REQUEST_CONTACT = 1;
const val PERMISSION_REQUEST_CODE = 100;

class MainActivity : AppCompatActivity() {
    private var phoneNumber: String = "";
    private var name: String = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        facebook.setOnClickListener {
            // val pageNum = "6464425484";
            Intent(ACTION_VIEW).apply {
                try {
                    data = Uri.parse("fb://page/");
                    startActivity(this);
                } catch (e: Exception) {
                    data = Uri.parse("https://m.facebook.com");
                    startActivity(this);
                }
            }
        }

        contacts.setOnClickListener {
            if (phoneNumber != "") {
                val intent = Intent(ACTION_DIAL)
                intent.data = Uri.parse("tel:$phoneNumber");
                startActivity(intent)
            } else {
                if (checkPermission()) {
                    val pickContactIntent =
                        Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                    startActivityForResult(pickContactIntent, REQUEST_CONTACT)
                    //check
                    val packageManager: PackageManager = packageManager
                    val resolvedActivity: ResolveInfo? =
                        packageManager.resolveActivity(
                            pickContactIntent,
                            PackageManager.MATCH_DEFAULT_ONLY
                        )
                    if (resolvedActivity == null) {
                        it.isEnabled = false
                    }
                }
                else{
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        requestPermissions(arrayOf(READ_CONTACTS, WRITE_CONTACTS),
                            PERMISSION_REQUEST_CODE)
                    }
                }
            }
        }

        email.setOnClickListener {
            val recipient = arrayOf("alamodi326@gmail.com");
            val subject = "the subject";
            val message = "the message ...";
            Intent(Intent.ACTION_SEND).apply {
                data = Uri.parse("mailto:")
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, recipient);
                putExtra(Intent.EXTRA_SUBJECT, subject);
                putExtra(Intent.EXTRA_TEXT, message);
                startActivity(this);
            }
        }
        store.setOnClickListener {
            Intent(ACTION_VIEW).apply {
                data = Uri.parse("market://");
                startActivity(this);
            }
        }

        location.setOnClickListener {
            val intent = Intent(ACTION_VIEW)
            intent.data = Uri.parse("geo:150.2,155.9");
            startActivity(intent)
        }

        web.setOnClickListener {
            // # Open URL in browser
            val uri: Uri = Uri.parse("https://developer.android.com");
            val intent = Intent(ACTION_VIEW, uri)
            startActivity(intent)
        }

        close.setOnClickListener {
            finish();
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode != RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                try {
                    val cursor = contactUri?.let {
                        contentResolver
                            .query(it, null, null, null, null)
                    }
                    cursor?.let {
                        // Verify cursor contains at least one result
                        if (it.count == 0) {
                            return
                        }
                        it.moveToFirst()
                        phoneNumber =
                            it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        name =
                            it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        it.close();
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private fun checkPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val result1 =
                checkSelfPermission(READ_CONTACTS);
            val result2 = checkSelfPermission(WRITE_CONTACTS);
            (result1 == PackageManager.PERMISSION_GRANTED
                    && result2 == PackageManager.PERMISSION_GRANTED)
        } else {
            true
        }
    }

}