package com.codingraz.bootcamp.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingraz.bootcamp.adapter.ConversationAdapter
import com.codingraz.bootcamp.databinding.FragmentConversationBinding
import com.codingraz.bootcamp.model.ConversationModel
import com.codingraz.bootcamp.model.UserModel
import com.codingraz.bootcamp.utils.fragmentAdd
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream


class ConversationFragment : Fragment() {

    private lateinit var binding: FragmentConversationBinding

    private val convs = arrayListOf<ConversationModel>()

    private var convAdapter: ConversationAdapter? = null

    private val db = Firebase.firestore

    private var userModel: UserModel? = null
    private var userAnotherModel: UserModel? = null

    private var roomId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

            userModel = it.getParcelable("USER_MODEL") as? UserModel? // Me
            userAnotherModel =
                it.getParcelable("USER_ANOTHER_MODEL") as? UserModel? // Selected User

            roomId = "${userModel?.userId}${userAnotherModel?.userId}".toCharArray().sorted()
                .joinToString("")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentConversationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        convAdapter = ConversationAdapter(convs, userModel?.userId ?: "")

        binding.rvConv.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, true)
        binding.rvConv.adapter = convAdapter

        convAdapter?.setItemClick {
            val bundle = Bundle().apply {
//            putParcelable()
            }

            fragmentAdd(activity, ConversationFragment(), bundle)
        }


        loadData()

        binding.btnSend.setOnClickListener {

            val msg = binding.etMessage.text?.toString()
            binding.etMessage.text?.clear()

            val conv = ConversationModel(
                userModel?.userId ?: "",
                userModel?.userName ?: "",
                userModel?.userImage ?: "",
                msg ?: "",
                "",
                null,
                ""
            )

            sendMessage(conv)

        }

        binding.btnCamera.setOnClickListener {

            ImagePicker.with(this)
                .compress(800)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    512,
                    512
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }

        }

    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {

                data?.data?.let {
                    sendImageMessage(it)
                }


            } else if (resultCode == ImagePicker.RESULT_ERROR) {
//                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }


    val storage = Firebase.storage.reference

    private fun sendImageMessage(imgUri: Uri) {


        val f = File(imgUri.path)

        val fName = "${System.currentTimeMillis()}_${f.name}"

        val mountainsRef = storage.child("images").child(fName)

        val steam = FileInputStream(f)
        val uploadTask = mountainsRef.putStream(steam)

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            mountainsRef.downloadUrl.addOnSuccessListener { uri ->
                // Got the download URL for 'users/me/profile.png'

                val dUrl = uri.toString().split("&token").first()

                val conv = ConversationModel(
                    userModel?.userId ?: "",
                    userModel?.userName ?: "",
                    userModel?.userImage ?: "",
                    "",
                    dUrl ?: "",
                    null,
                    ""
                )

                sendMessage(conv)

            }.addOnFailureListener {
                // Handle any errors
            }
        }

    } // sendImageMessage End Here ==========

    private fun loadData() {


        getMessageRef()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
//                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                snapshots?.documents?.let { docs ->
                    convs.clear()
                    for (dc in docs) {

                        dc.data?.let { d ->

                            val uId = d.get("userId") as? String?
                            val uName = d.get("userName") as? String?
                            val uImg = d.get("userImage") as? String?
                            val msg = d.get("message") as? String?
                            val iMsg = d.get("imageMessage") as? String?
                            val ts = d.get("timestamp") as? Timestamp?
                            val tsl = d.get("temeStampLocal") as? String?

                            val t: String? = ts?.toDate()?.toLocaleString()

                            convs.add(
                                ConversationModel(
                                    uId ?: "",
                                    uName ?: "",
                                    uImg ?: "",
                                    msg ?: "",
                                    iMsg ?: "",
                                    null,
                                    t ?: ""

                                )
                            )

                        }
                    }

                    activity?.runOnUiThread {
                        convAdapter?.notifyDataSetChanged()
                        convAdapter?.itemCount?.let {
                            if (it > 0) {
                                Handler().postDelayed(Runnable {
                                    binding.rvConv.smoothScrollToPosition(0)
                                }, 1000)
                            }
                        }
                    }

                }


            }

    } // loadData End Here==========

    private fun sendMessage(cm: ConversationModel) {

// Add a new document with a generated ID
        getMessageRef()
            .add(cm)
            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

//                loadData()

                // Update Last Message
                var msg = cm.message ?: ""
                if (!cm.imageMessage.isNullOrEmpty()) {
                    msg = "Received Image"
                }

                userModel?.let {
                    val myUpdatedUser = it
                    myUpdatedUser.lastMessage = msg
                    db.collection("user").document(cm.userId ?: "").set(myUpdatedUser)
                }

            }
            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
            }

    } // sendMessage End Here ==============

    private fun getMessageRef(): CollectionReference {
        return db.collection("room")
            .document(roomId)
            .collection("Message")
    }

}